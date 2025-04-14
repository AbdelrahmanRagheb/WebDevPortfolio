package com.taskasync.userservice.service.impl;

import com.taskasync.userservice.dto.notification.EventType;
import com.taskasync.userservice.entity.UserNotification;
import com.taskasync.userservice.repository.UserNotificationRepository;
import com.taskasync.userservice.service.INotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class INotificationServiceImpl implements INotificationService {

    private final UserNotificationRepository notificationRepository;

    public INotificationServiceImpl(UserNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createTaskAssignmentNotification(Long userId, EventType type, String message,
                                                 Long taskId, String taskTitle, Long assignedById, String assignedByName) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("task_id", taskId);
        metadata.put("task_title", taskTitle);
        metadata.put("assigned_by", assignedById);
        metadata.put("assigned_by_name", assignedByName);
        metadata.put("role", "ASSIGNEE");



        UserNotification notification = new UserNotification(
                userId,
                type,
                message,
                metadata
        );
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public void createCommentNotification(Long userId, EventType type, Long taskId, String taskTitle, Long commentId,
                                          String commentPreview, Long commenterId, String commenterName) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("task_id", taskId);
        metadata.put("task_title", taskTitle);
        metadata.put("comment_id", commentId);
        metadata.put("comment_preview", commentPreview);
        metadata.put("commenter_id", commenterId);
        metadata.put("commenter_name", commenterName);

        String message = String.format("%s commented on task '%s': %s",
                commenterName, taskTitle, commentPreview);

        UserNotification notification = new UserNotification(
                userId,
                type,
                message,
                metadata
        );
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public List<UserNotification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<UserNotification> getTaskNotifications(Long userId, Long taskId) {
        return notificationRepository.findByUserIdAndTaskId(userId, taskId);
    }
}
