package com.taskasync.notificationservice.functions;

import com.taskasync.notificationservice.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.function.Consumer;

@Configuration
public class NotificationCenter {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCenter.class);
    private final StreamBridge streamBridge;

    public NotificationCenter(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<TaskNotification> receiveTaskNotification() {
        return notification -> {
            logger.info("Received notification of type {}: {}", notification.getClass().getSimpleName(), notification);
            try {

                if (notification instanceof TaskCreationNotificationDto taskCreation) {
                    logger.info("Processing task creation notification: {}", taskCreation);
                } else if (notification instanceof TaskUpdatedNotificationDto taskUpdate) {
                    logger.info("Processing task update notification: {}", taskUpdate);
                } else if (notification instanceof TaskCommentNotificationDto taskComment) {
                    logger.info("Processing task comment notification: {}", taskComment);
                } else if (notification instanceof TaskDeletedNotificationDto taskDeleted) {
                    logger.info("Processing task deleted notification: {}", taskDeleted);
                }
                sendToUserService(notification);
                // Forward to user-service

            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize", e);
            }
        };
    }


    private void sendToUserService(TaskNotification notification) {
        try {
            streamBridge.send("forwardNotification-out-0", notification);
            logger.info("Sent notification to user-service: {}", notification);
        } catch (Exception e) {
            logger.error("Failed to send notification to user-service: {}", notification, e);
            throw new RuntimeException("Failed to send to user-service: " + e.getMessage(), e);
        }
    }
}