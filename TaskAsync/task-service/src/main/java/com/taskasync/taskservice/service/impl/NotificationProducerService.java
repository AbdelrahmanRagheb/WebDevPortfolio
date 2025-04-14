package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.ChangeDetails;
import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.dto.notification.*;
import com.taskasync.taskservice.entity.Task;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationProducerService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationProducerService.class);
    private StreamBridge streamBridge;

    private void sendTaskNotification(TaskNotification notification) {
        try {
            logger.info("Received notification of type {}: {}", notification.getClass().getSimpleName(), notification);
            boolean result = streamBridge.send("taskNotification-out-0", notification);
            logger.info("Is the communication request successfully processed? {}", result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    public void sendTaskCreatedNotification(EventType eventType, Long taskId, String taskTitle, Long creatorId, String creatorUsername, Map<Long, String> assignedUsers) {
        TaskCreationNotificationDto notification = new TaskCreationNotificationDto();
        notification.setEventType(eventType);

        TaskCreationNotificationDto.NotificationPayload payload = new TaskCreationNotificationDto.NotificationPayload();
        payload.setTaskId(taskId);
        payload.setTitle(taskTitle);
        payload.setCreatorId(creatorId);
        payload.setCreatorUsername(creatorUsername);

        if (assignedUsers == null || assignedUsers.isEmpty()) {
            assignedUsers = new HashMap<>();
            assignedUsers.put(creatorId, "creator");
        } else if (!assignedUsers.containsKey(creatorId)) {
            assignedUsers.put(creatorId, "creator");
        }

        for (Map.Entry<Long, String> entry : assignedUsers.entrySet()) {
            UserNotificationAssignedRoleMessage roleMessage = new UserNotificationAssignedRoleMessage();
            roleMessage.setId(entry.getKey());
            roleMessage.setRole(entry.getValue());
            if ("creator".equals(entry.getValue())) {
                roleMessage.setMsg("You have successfully created a new task titled \"" + taskTitle + "\".");
            } else {
                roleMessage.setMsg("A new task titled \"" + taskTitle + "\" has been created, and you have been assigned the role of \"" + entry.getValue() + "\" for this task.");
            }
            notification.getRoleMessages().add(roleMessage);
        }

        notification.setPayload(payload);
        sendTaskNotification(notification);
    }

    public void notifyAssignedUsers(EventType eventType, Task existingTask, TaskDto taskDto, TaskHistoryDto savedTaskHistory) {
        // Get old and new assigned users
        Map<Long, String> oldAssignedUsers = existingTask.getAssignedUsers();
        Map<Long, String> newAssignedUsers = taskDto.getAssignedUsers();

        // Extract field changes from TaskHistoryDto (excluding assignedUsers to avoid duplication)
        List<ChangeDetails.Change> fieldChanges = savedTaskHistory.getChangeDetails().getChanges().stream()
                .filter(change -> !change.getField().equals("assignedUsers"))
                .collect(Collectors.toList());

        // Prepare a single block notification
        TaskUpdatedNotificationDto notification = new TaskUpdatedNotificationDto();
        notification.setTaskId(existingTask.getId());
        notification.setTitle(existingTask.getTitle());
        notification.setCreatorId(taskDto.getCreatorId());
        notification.setCreatorUsername(taskDto.getCreatorUsername());

        // Identify all affected users
        Set<Long> allAffectedUserIds = new HashSet<>();
        allAffectedUserIds.addAll(oldAssignedUsers.keySet());
        allAffectedUserIds.addAll(newAssignedUsers.keySet());

        for (Long userId : allAffectedUserIds) {
            String oldRole = oldAssignedUsers.get(userId);
            String newRole = newAssignedUsers.get(userId);

            // Case 1: User was removed (in old but not in new)
            if (oldRole != null && newRole == null) {
                notification.addMessage(userId, new TaskUpdatedNotificationDto.RoleChangeMessage(oldRole, null));
                continue;
            }

            // Case 2: User is still assigned (in new assignedUsers)
            if (newRole != null) {
                // Check for role change
                if (oldRole == null) {
                    // Newly assigned
                    notification.addMessage(userId, new TaskUpdatedNotificationDto.RoleChangeMessage(null, newRole));
                } else if (!oldRole.equals(newRole)) {
                    // Role changed
                    notification.addMessage(userId, new TaskUpdatedNotificationDto.RoleChangeMessage(oldRole, newRole));
                }

                // Add field changes if any (for assigned users only)
                if (!fieldChanges.isEmpty()) {
                    notification.addMessage(userId, new TaskUpdatedNotificationDto.FieldChangeMessage(fieldChanges));
                }
            }
        }

        // Only send the notification if there are messages to send
        if (!notification.getUserNotifications().isEmpty()) {
            sendTaskNotification(notification);
        }
    }

    public void sendTaskDeletedNotification(Long taskId, String taskTitle, Long creatorId, Map<Long, String> assignedUsers) {
        TaskDeletedNotificationDto notification = new TaskDeletedNotificationDto();
        notification.setTaskId(taskId);
        notification.setTitle(taskTitle);
        notification.setCreatorId(creatorId);

        notification.setNotificationDateTime(LocalDateTime.now());

        // Ensure creator is included if not already in assignedUsers
        Set<Long> notifiedUserIds = new HashSet<>();
        if (assignedUsers != null) {
            notifiedUserIds.addAll(assignedUsers.keySet());
        }
        notifiedUserIds.add(creatorId); // Always notify the creator

        notifiedUserIds.forEach(notification::addNotifiedUser);
        sendTaskNotification(notification);
    }

    public void sendTaskCommentNotification(Long taskId, String taskTitle, Long creatorId, Map<Long, String> assignedUsers, Long commenterId, String commentText) {
        TaskCommentNotificationDto notification = new TaskCommentNotificationDto();
        notification.setTaskId(taskId);
        notification.setTitle(taskTitle);
        notification.setCreatorId(creatorId);
        notification.setNotificationDateTime(LocalDateTime.now());

        // Create the comment message
        TaskCommentNotificationDto.CommentMessage commentMessage = new TaskCommentNotificationDto.CommentMessage(
                commenterId, commentText, LocalDateTime.now()
        );

        // Notify all relevant users (creator + assigned users)
        Set<Long> notifiedUserIds = new HashSet<>();
        if (assignedUsers != null) {
            notifiedUserIds.addAll(assignedUsers.keySet());
        }
        notifiedUserIds.add(creatorId);

        // Add the comment message for each user
        notifiedUserIds.forEach(userId -> notification.addComment(userId, commentMessage));

        sendTaskNotification(notification);
    }
}