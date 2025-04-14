package com.taskasync.taskservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.taskasync.taskservice.dto.ChangeDetails;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TaskUpdatedNotificationDto implements TaskNotification {
    private Long taskId;
    private String title;
    private Long creatorId;
    private String creatorUsername;
    private LocalDateTime notificationDateTime;
    private Map<Long, List<NotificationMessage>> userNotifications = new HashMap<>();

    @Override
    public EventType getEventType() {
        return EventType.TASK_UPDATED;
    }

    public void addMessage(Long userId, NotificationMessage message) {
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
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
        public FieldChangeMessage(List<ChangeDetails.Change> changes) {
            this.eventType = EventType.TASK_UPDATED;
            this.changes = changes;
        }
    }

    @Data
    public static class RoleChangeMessage extends NotificationMessage {
        private String oldRole;
        private String newRole;
        public RoleChangeMessage(String oldRole, String newRole) {
            this.eventType = oldRole == null ? EventType.TASK_ROLE_ADDED :
                    newRole == null ? EventType.TASK_ROLE_REMOVED :
                            EventType.TASK_ROLE_CHANGED;
            this.oldRole = oldRole;
            this.newRole = newRole;
        }
    }
}