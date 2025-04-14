package com.taskasync.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskDeletedNotificationDto implements TaskNotification {
    private Long taskId;
    private String title;
    private Long creatorId;
    private String creatorUsername;
    private LocalDateTime notificationDateTime;
    private List<Long> notifiedUserIds = new ArrayList<>();

    @Override
    public EventType getEventType() {
        return EventType.TASK_DELETED;
    }

    public void addNotifiedUser(Long userId) {
        notifiedUserIds.add(userId);
    }
}