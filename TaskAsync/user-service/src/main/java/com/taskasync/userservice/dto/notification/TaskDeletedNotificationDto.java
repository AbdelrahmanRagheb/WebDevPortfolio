package com.taskasync.userservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data

public class TaskDeletedNotificationDto implements TaskNotification {
    private EventType eventType = EventType.TASK_DELETED;
    private Long taskId;
    private String title;
    private Long creatorId;
    private String creatorUsername;
    private LocalDateTime notificationDateTime = LocalDateTime.now();
    private List<Long> notifiedUserIds = new ArrayList<>();

    @Override
    public Long getTaskId() {
        return taskId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Long getCreatorId() {
        return creatorId;
    }

    @Override
    public String getCreatorUsername() {
        return creatorUsername;
    }

    @Override
    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void addNotifiedUser(Long userId) {
        notifiedUserIds.add(userId);
    }

    public List<Long> getNotifiedUserIds() {
        return notifiedUserIds;
    }
}