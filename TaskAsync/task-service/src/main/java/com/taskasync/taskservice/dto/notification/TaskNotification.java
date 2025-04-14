package com.taskasync.taskservice.dto.notification;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "__type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskCreationNotificationDto.class, name = "TaskCreationNotificationDto"),
        @JsonSubTypes.Type(value = TaskUpdatedNotificationDto.class, name = "TaskUpdatedNotificationDto"),
        @JsonSubTypes.Type(value = TaskCommentNotificationDto.class, name = "TaskCommentNotificationDto"),
        @JsonSubTypes.Type(value = TaskDeletedNotificationDto.class, name = "TaskDeletedNotificationDto")
})
public interface TaskNotification {
    EventType getEventType();
    Long getTaskId();
    String getTitle();
    Long getCreatorId();
    String getCreatorUsername();
    LocalDateTime getNotificationDateTime();
}