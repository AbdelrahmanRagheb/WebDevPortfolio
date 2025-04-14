package com.taskasync.userservice.service;

import com.taskasync.userservice.dto.notification.EventType;
import com.taskasync.userservice.entity.UserNotification;

import java.util.List;

public interface INotificationService {
    void createTaskAssignmentNotification(Long userId, EventType type, String message, Long taskId, String taskTitle, Long assignedById, String assignedByName);

    void createCommentNotification(Long userId, EventType type, Long taskId, String taskTitle, Long commentId,
                                   String commentPreview, Long commenterId, String commenterName);

    List<UserNotification> getUserNotifications(Long userId);


    List<UserNotification> getTaskNotifications(Long userId, Long taskId);
}