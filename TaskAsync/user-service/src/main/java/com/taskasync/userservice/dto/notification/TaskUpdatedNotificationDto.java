package com.taskasync.userservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data

public class TaskUpdatedNotificationDto implements TaskNotification {
    private EventType eventType = EventType.TASK_UPDATED;
    private NotificationPayload payload;
    private Map<Long, List<NotificationMessage>> userNotifications = new HashMap<>();

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
        return LocalDateTime.now();
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FieldChangeMessage.class, name = "FieldChangeMessage"),
            @JsonSubTypes.Type(value = RoleChangeMessage.class, name = "RoleChangeMessage")
    })
    @Data
    public static abstract class NotificationMessage {
        protected EventType eventType;
    }

    @Data
    public static class FieldChangeMessage extends NotificationMessage {
        private List<ChangeDetails.Change> changes;
        public FieldChangeMessage() {
            this.eventType = EventType.TASK_UPDATED;
        }
        public FieldChangeMessage(List<ChangeDetails.Change> changes) {
            this.eventType = EventType.TASK_UPDATED;
            this.changes = changes;
        }
    }

    @Data
    public static class RoleChangeMessage extends NotificationMessage {
        private String oldRole;
        private String newRole;
        public RoleChangeMessage() {}
        public RoleChangeMessage(String oldRole, String newRole) {
            this.eventType = oldRole == null ? EventType.TASK_ROLE_ADDED :
                    newRole == null ? EventType.TASK_ROLE_REMOVED :
                            EventType.TASK_ROLE_CHANGED;
            this.oldRole = oldRole;
            this.newRole = newRole;
        }
    }

    @Data
    public static class NotificationPayload {
        private Long taskId;
        private String title;
        private Long creatorId;
        private String creatorUsername;
        private String message;
    }
}