package com.taskasync.notificationservice.dto;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data

public class TaskCreationNotificationDto implements TaskNotification {
    private EventType eventType;
    private NotificationPayload payload;
    private List<UserNotificationAssignedRoleMessage> roleMessages = new ArrayList<>();

    @Override
    public Long getTaskId() {
        return payload != null ? payload.getTaskId() : null;
    }

    @Override
    public String getTitle() {
        return payload != null ? payload.getTitle() : null;
    }

    @Override
    public Long getCreatorId() {
        return payload != null ? payload.getCreatorId() : null;
    }

    @Override
    public String getCreatorUsername() {
        return payload != null ? payload.getCreatorUsername() : null;
    }

    @Override
    public LocalDateTime getNotificationDateTime() {
        return LocalDateTime.now(); // Or store this explicitly if needed
    }

    @Data
    public static class NotificationPayload {
        private Long taskId;
        private String title;
        private Long creatorId;
        private String creatorUsername;
        private String message; // Used in comment/deleted notifications
    }
}