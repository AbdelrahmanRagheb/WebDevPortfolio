package com.taskasync.userservice.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskasync.userservice.dto.notification.*;
import com.taskasync.userservice.entity.UserNotification;
import com.taskasync.userservice.entity.UserTask;
import com.taskasync.userservice.repository.UserNotificationRepository;
import com.taskasync.userservice.repository.UserRepository;
import com.taskasync.userservice.repository.UserTaskRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class NotificationCenter {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCenter.class);
    private final ObjectMapper objectMapper;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final UserTaskRepository userTaskRepository;

    public NotificationCenter(ObjectMapper objectMapper,
                              UserNotificationRepository userNotificationRepository,
                              UserRepository userRepository,
                              UserTaskRepository userTaskRepository) {
        this.objectMapper = objectMapper;
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
        this.userTaskRepository = userTaskRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("NotificationCenter initialized with ObjectMapper: {}, UserNotificationRepository: {}, UserRepository: {}, UserTaskRepository: {}",
                objectMapper != null, userNotificationRepository != null, userRepository != null, userTaskRepository != null);
    }

    @Bean
    public Consumer<Message<byte[]>> receiveUserNotification() {
        return message -> {
            logger.info("Message received from channel");
            String json = new String(message.getPayload());
            logger.info("Raw message payload: {}", json);
            try {
                TaskNotification notification = objectMapper.readValue(json, TaskNotification.class);
                logger.info("Received message of type {}: {}", notification.getClass().getSimpleName(), notification);
                switch (notification.getEventType()) {
                    case TASK_CREATED -> handleTaskCreation((TaskCreationNotificationDto) notification);
                    case TASK_UPDATED, TASK_ROLE_ADDED, TASK_ROLE_CHANGED, TASK_ROLE_REMOVED ->
                            handleTaskUpdate((TaskUpdatedNotificationDto) notification);
                    case TASK_COMMENT_ADDED -> handleTaskComment((TaskCommentNotificationDto) notification);
                    case TASK_DELETED -> handleTaskDeleted((TaskDeletedNotificationDto) notification);
                    default -> logger.warn("Unknown notification type: {}", notification.getEventType());
                }
            } catch (Exception e) {
                logger.error("Failed to process notification for payload: {}", json, e);
                throw new RuntimeException("Failed to process notification: " + e.getMessage(), e);
            }
        };
    }

    private void handleTaskCreation(TaskCreationNotificationDto notification) {
        logger.info("Processing task creation: {}", notification);
        notification.getNotifiedUserIds().forEach(userId -> {
            if (!userRepository.existsById(userId)) {
                logger.warn("User with ID {} does not exist, skipping notification and task assignment", userId);
                return;
            }

            String role = notification.getRoleMessages().stream()
                    .filter(msg -> userId.equals(msg.getId()))
                    .findFirst()
                    .map(UserNotificationAssignedRoleMessage::getRole)
                    .orElse("UNKNOWN");

            UserNotification userNotification = new UserNotification();
            userNotification.setUserId(userId);
            userNotification.setNotificationType(EventType.TASK_CREATED);
            userNotification.setMessage(String.format("Task '%s' (ID: %d) created by %s. Assigned role: %s",
                    notification.getTitle(), notification.getTaskId(), notification.getCreatorUsername(), role));
            userNotification.setMetadata(createMetadata(notification.getTaskId(), notification.getCreatorId(),
                    notification.getCreatorUsername(), "Task created, role: " + role));
            userNotificationRepository.save(userNotification);
            logger.info("Saved notification for userId: {}", userId);

            UserTask userTask = new UserTask();
            userTask.setUser(userRepository.findById(userId).orElseThrow());
            userTask.setTaskId(notification.getTaskId());
            userTask.setRoleInTask(role);
            userTaskRepository.save(userTask);
            logger.info("Saved task assignment for userId: {} with role: {}", userId, role);
        });
    }

    private void handleTaskUpdate(TaskUpdatedNotificationDto notification) {
        logger.info("Processing task update: {}", notification);
        notification.getUserNotifications().forEach((userId, messages) -> {
            if (!userRepository.existsById(userId)) {
                logger.warn("User with ID {} does not exist, skipping update", userId);
                return;
            }
            messages.forEach(message -> {
                if (message instanceof TaskUpdatedNotificationDto.FieldChangeMessage fieldChange) {
                    fieldChange.getChanges().forEach(change -> {
                        UserNotification userNotification = new UserNotification();
                        userNotification.setUserId(userId);
                        userNotification.setNotificationType(EventType.TASK_UPDATED);
                        userNotification.setMessage(String.format("Task '%s' (ID: %d) updated: %s changed from '%s' to '%s'",
                                notification.getTitle(), notification.getTaskId(), change.getField(),
                                change.getOldValue(), change.getNewValue()));
                        userNotification.setMetadata(createMetadata(notification.getTaskId(), notification.getCreatorId(),
                                notification.getCreatorUsername(), "Field: " + change.getField()));
                        userNotificationRepository.save(userNotification);
                        logger.info("Saved field update notification for userId: {}", userId);
                    });
                } else if (message instanceof TaskUpdatedNotificationDto.RoleChangeMessage roleChange) {
                    UserNotification userNotification = new UserNotification();
                    userNotification.setUserId(userId);
                    userNotification.setNotificationType(roleChange.getEventType());
                    String messageText;
                    String metadataAction;

                    switch (roleChange.getEventType()) {
                        case TASK_ROLE_ADDED -> {
                            messageText = String.format("Task '%s' (ID: %d): You have been assigned the role '%s' by %s",
                                    notification.getTitle(), notification.getTaskId(), roleChange.getNewRole(),
                                    notification.getCreatorUsername());
                            metadataAction = "Role assigned: " + roleChange.getNewRole();

                            UserTask userTask = new UserTask();
                            userTask.setUser(userRepository.findById(userId).orElseThrow());
                            userTask.setTaskId(notification.getTaskId());
                            userTask.setRoleInTask(roleChange.getNewRole());
                            userTaskRepository.save(userTask);
                            logger.info("Added task role for userId: {} as: {}", userId, roleChange.getNewRole());
                        }
                        case TASK_ROLE_CHANGED -> {
                            messageText = String.format("Task '%s' (ID: %d): Your role changed from '%s' to '%s' by %s",
                                    notification.getTitle(), notification.getTaskId(), roleChange.getOldRole(),
                                    roleChange.getNewRole(), notification.getCreatorUsername());
                            metadataAction = "Role changed from " + roleChange.getOldRole() + " to " + roleChange.getNewRole();

                            UserTask userTask = userTaskRepository.findByUserIdAndTaskId(userId, notification.getTaskId())
                                    .orElseGet(() -> {
                                        UserTask newTask = new UserTask();
                                        newTask.setUser(userRepository.findById(userId).orElseThrow());
                                        newTask.setTaskId(notification.getTaskId());
                                        return newTask;
                                    });
                            userTask.setRoleInTask(roleChange.getNewRole());
                            userTaskRepository.save(userTask);
                            logger.info("Updated task role for userId: {} to: {}", userId, roleChange.getNewRole());
                        }
                        case TASK_ROLE_REMOVED -> {
                            messageText = String.format("Task '%s' (ID: %d): Your role '%s' has been removed by %s",
                                    notification.getTitle(), notification.getTaskId(), roleChange.getOldRole(),
                                    notification.getCreatorUsername());
                            metadataAction = "Role removed: " + roleChange.getOldRole();

                            userTaskRepository.deleteByUserIdAndTaskId(userId, notification.getTaskId());
                            logger.info("Deleted task assignment for userId: {} from taskId: {}", userId, notification.getTaskId());
                        }
                        default -> {
                            logger.warn("Unhandled role change event type: {}", roleChange.getEventType());
                            return;
                        }
                    }

                    userNotification.setMessage(messageText);
                    userNotification.setMetadata(createMetadata(notification.getTaskId(), notification.getCreatorId(),
                            notification.getCreatorUsername(), metadataAction));
                    userNotificationRepository.save(userNotification);
                    logger.info("Saved role change notification for userId: {}", userId);
                }
            });
        });
    }

    private void handleTaskComment(TaskCommentNotificationDto notification) {
        logger.info("Processing task comment: {}", notification);
        notification.getUserNotifications().forEach((userId, comments) -> {
            if (!userRepository.existsById(userId)) {
                logger.warn("User with ID {} does not exist, skipping comment notification", userId);
                return;
            }
            comments.forEach(comment -> {
                UserNotification userNotification = new UserNotification();
                userNotification.setUserId(userId);
                userNotification.setNotificationType(EventType.TASK_COMMENT_ADDED);
                userNotification.setMessage(String.format("New comment on task '%s' (ID: %d) by %s: '%s'",
                        notification.getTitle(), notification.getTaskId(), comment.getCommenterUsername(),
                        comment.getCommentText()));
                userNotification.setMetadata(createMetadata(notification.getTaskId(), comment.getCommenterId(),
                        comment.getCommenterUsername(), "Comment added"));
                userNotificationRepository.save(userNotification);
                logger.info("Saved comment notification for userId: {}", userId);
            });
        });
    }

    private void handleTaskDeleted(TaskDeletedNotificationDto notification) {
        logger.info("Processing task deletion: {}", notification);
        notification.getNotifiedUserIds().forEach(userId -> {
            if (!userRepository.existsById(userId)) {
                logger.warn("User with ID {} does not exist, skipping deletion notification", userId);
                return;
            }
            UserNotification userNotification = new UserNotification();
            userNotification.setUserId(userId);
            userNotification.setNotificationType(EventType.TASK_DELETED);
            userNotification.setMessage(String.format("Task '%s' (ID: %d) deleted by %s",
                    notification.getTitle(), notification.getTaskId(), notification.getCreatorUsername()));
            userNotification.setMetadata(createMetadata(notification.getTaskId(), notification.getCreatorId(),
                    notification.getCreatorUsername(), "Task deleted"));
            userNotificationRepository.save(userNotification);
            logger.info("Saved deletion notification for userId: {}", userId);
        });
    }

    private Map<String, Object> createMetadata(Long taskId, Long creatorId, String creatorUsername, String action) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("taskId", taskId);
        metadata.put("creatorId", creatorId);
        metadata.put("creatorUsername", creatorUsername);
        metadata.put("action", action);
        return metadata;
    }
}