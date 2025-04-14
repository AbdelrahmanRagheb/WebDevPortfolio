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

        Map<Long, String> oldAssignedUsers = existingTask.getAssignedUsers();
        Map<Long, String> newAssignedUsers = taskDto.getAssignedUsers();


        List<ChangeDetails.Change> fieldChanges = savedTaskHistory.getChangeDetails().getChanges().stream()
                .filter(change -> !change.getField().equals("assignedUsers"))
                .collect(Collectors.toList());


        TaskUpdatedNotificationDto notification = new TaskUpdatedNotificationDto();
        notification.setTaskId(existingTask.getId());
        notification.setTitle(existingTask.getTitle());
        notification.setCreatorId(taskDto.getCreatorId());
        notification.setCreatorUsername(taskDto.getCreatorUsername());


        Set<Long> allAffectedUserIds = new HashSet<>();
        allAffectedUserIds.addAll(oldAssignedUsers.keySet());
        allAffectedUserIds.addAll(newAssignedUsers.keySet());

        for (Long userId : allAffectedUserIds) {
            String oldRole = oldAssignedUsers.get(userId);
            String newRole = newAssignedUsers.get(userId);


            if (oldRole != null && newRole == null) {
                notification.addMessage(userId, new TaskUpdatedNotificationDto.RoleChangeMessage(oldRole, null));
                continue;
            }


            if (newRole != null) {

                if (oldRole == null) {

                    notification.addMessage(userId, new TaskUpdatedNotificationDto.RoleChangeMessage(null, newRole));
                } else if (!oldRole.equals(newRole)) {

                    notification.addMessage(userId, new TaskUpdatedNotificationDto.RoleChangeMessage(oldRole, newRole));
                }


                if (!fieldChanges.isEmpty()) {
                    notification.addMessage(userId, new TaskUpdatedNotificationDto.FieldChangeMessage(fieldChanges));
                }
            }
        }


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


        Set<Long> notifiedUserIds = new HashSet<>();
        if (assignedUsers != null) {
            notifiedUserIds.addAll(assignedUsers.keySet());
        }
        notifiedUserIds.add(creatorId);

        notifiedUserIds.forEach(notification::addNotifiedUser);
        sendTaskNotification(notification);
    }

    public void sendTaskCommentNotification(Long taskId, String taskTitle, Long creatorId, Map<Long, String> assignedUsers, Long commenterId, String commentText) {
        TaskCommentNotificationDto notification = new TaskCommentNotificationDto();
        notification.setTaskId(taskId);
        notification.setTitle(taskTitle);
        notification.setCreatorId(creatorId);
        notification.setNotificationDateTime(LocalDateTime.now());


        TaskCommentNotificationDto.CommentMessage commentMessage = new TaskCommentNotificationDto.CommentMessage(
                commenterId, commentText, LocalDateTime.now()
        );


        Set<Long> notifiedUserIds = new HashSet<>();
        if (assignedUsers != null) {
            notifiedUserIds.addAll(assignedUsers.keySet());
        }
        notifiedUserIds.add(creatorId);


        notifiedUserIds.forEach(userId -> notification.addComment(userId, commentMessage));

        sendTaskNotification(notification);
    }
}