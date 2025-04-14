package com.taskasync.userservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data

public class TaskCreationNotificationDto implements TaskNotification {
    private EventType eventType = EventType.TASK_CREATED; // Default value
    private NotificationPayload payload;
    private List<UserNotificationAssignedRoleMessage> roleMessages = new ArrayList<>();

    @Override
    public EventType getEventType() {
        return eventType;
    }

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

    public Set<Long> getNotifiedUserIds() {
        return roleMessages.stream()
                .map(UserNotificationAssignedRoleMessage::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
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