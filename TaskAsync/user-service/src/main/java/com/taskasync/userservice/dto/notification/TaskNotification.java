package com.taskasync.userservice.dto.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskCreationNotificationDto.class, name = "TaskCreationNotificationDto"),
        @JsonSubTypes.Type(value = TaskUpdatedNotificationDto.class, name = "TaskUpdatedNotificationDto"),
        @JsonSubTypes.Type(value = TaskCommentNotificationDto.class, name = "TaskCommentNotificationDto"),
        @JsonSubTypes.Type(value = TaskDeletedNotificationDto.class, name = "TaskDeletedNotificationDto"),
        @JsonSubTypes.Type(value = TaskRoleRemovedNotificationDto.class, name = "TaskRoleRemovedNotificationDto")
})
public interface TaskNotification {
    EventType getEventType();
    Long getTaskId();
    String getTitle();
    Long getCreatorId();
    String getCreatorUsername();
    LocalDateTime getNotificationDateTime();
}