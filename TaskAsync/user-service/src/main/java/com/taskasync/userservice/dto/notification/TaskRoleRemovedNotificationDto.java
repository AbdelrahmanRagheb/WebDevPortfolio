package com.taskasync.userservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data

public class TaskRoleRemovedNotificationDto implements TaskNotification {
    private EventType eventType = EventType.TASK_ROLE_REMOVED;
    private NotificationPayload payload;
    private Set<Long> removedUserIds = new HashSet<>();

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
        return removedUserIds;
    }

    @Data
    public static class NotificationPayload {
        private Long taskId;
        private String title;
        private Long creatorId;
        private String creatorUsername;
    }
}