package com.taskasync.userservice.service.impl;

import com.taskasync.userservice.dto.notification.EventType;
import com.taskasync.userservice.entity.User;
import com.taskasync.userservice.entity.UserNotification;
import com.taskasync.userservice.repository.UserNotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackages = "com.taskasync.userservice.service.impl")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class INotificationServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private INotificationServiceImpl notificationService;

    /**
     * Helper method to create and persist a User.
     */
    private User createUser() {
        User user = new User();
        user.setKeycloakSubjectId(UUID.randomUUID().toString());
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("test" + UUID.randomUUID() + "@example.com");
        user.setUsername("user" + UUID.randomUUID());
        entityManager.persistAndFlush(user);
        return user;
    }

    /**
     * Helper method to create a UserNotification with metadata.
     */
    private UserNotification createNotification(Long userId, EventType type, String message, Map<String, Object> metadata) {
        UserNotification notification = new UserNotification(
                userId, type, message, metadata
        );
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    @Test
    void testCreateTaskAssignmentNotification_Success() {
        // Arrange
        User user = createUser();
        Long taskId = 100L;
        String taskTitle = "Sample Task";
        Long assignedById = 2L;
        String assignedByName = "Admin";
        String message = "You've been assigned to task 'Sample Task'";
        EventType eventType = EventType.TASK_ROLE_ADDED;

        // Act
        notificationService.createTaskAssignmentNotification(
                user.getId(), eventType, message, taskId, taskTitle, assignedById, assignedByName
        );
        entityManager.flush();
        entityManager.clear();

        // Assert
        List<UserNotification> notifications = userNotificationRepository.findByUserId(user.getId());
        assertEquals(1, notifications.size());
        UserNotification notification = notifications.get(0);
        assertEquals(user.getId(), notification.getUserId());
        assertEquals(eventType, notification.getNotificationType());
        assertEquals(message, notification.getMessage());
        assertNotNull(notification.getCreatedAt());
        Map<String, Object> metadata = notification.getMetadata();
        assertEquals(taskId.longValue(), ((Number) metadata.get("task_id")).longValue());
        assertEquals(taskTitle, metadata.get("task_title"));
        assertEquals(assignedById.longValue(), ((Number) metadata.get("assigned_by")).longValue());
        assertEquals(assignedByName, metadata.get("assigned_by_name"));
        assertEquals("ASSIGNEE", metadata.get("role"));
    }

    @Test
    void testCreateCommentNotification_Success() {
        // Arrange
        User user = createUser();
        Long taskId = 100L;
        String taskTitle = "Sample Task";
        Long commentId = 200L;
        String commentPreview = "Great work!";
        Long commenterId = 2L;
        String commenterName = "Jane";
        EventType eventType = EventType.TASK_COMMENT_ADDED;
        String expectedMessage = "Jane commented on task 'Sample Task': Great work!";

        // Act
        notificationService.createCommentNotification(
                user.getId(), eventType, taskId, taskTitle, commentId, commentPreview, commenterId, commenterName
        );
        entityManager.flush();
        entityManager.clear();

        // Assert
        List<UserNotification> notifications = userNotificationRepository.findByUserId(user.getId());
        assertEquals(1, notifications.size());
        UserNotification notification = notifications.get(0);
        assertEquals(user.getId(), notification.getUserId());
        assertEquals(eventType, notification.getNotificationType());
        assertEquals(expectedMessage, notification.getMessage());
        assertNotNull(notification.getCreatedAt());
        Map<String, Object> metadata = notification.getMetadata();
        assertEquals(taskId.longValue(), ((Number) metadata.get("task_id")).longValue());
        assertEquals(taskTitle, metadata.get("task_title"));
        assertEquals(commentId.longValue(), ((Number) metadata.get("comment_id")).longValue());
        assertEquals(commentPreview, metadata.get("comment_preview"));
        assertEquals(commenterId.longValue(), ((Number) metadata.get("commenter_id")).longValue());
        assertEquals(commenterName, metadata.get("commenter_name"));
    }

    @Test
    void testGetUserNotifications_ReturnsNotifications() {
        // Arrange
        User user = createUser();
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("task_id", 100L);
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("task_id", 101L);
        UserNotification notification1 = createNotification(
                user.getId(), EventType.TASK_CREATED, "Task created", metadata1
        );
        UserNotification notification2 = createNotification(
                user.getId(), EventType.TASK_UPDATED, "Task updated", metadata2
        );
        userNotificationRepository.save(notification1);
        userNotificationRepository.save(notification2);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<UserNotification> notifications = notificationService.getUserNotifications(user.getId());

        // Assert
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getNotificationType() == EventType.TASK_CREATED));
        assertTrue(notifications.stream().anyMatch(n -> n.getNotificationType() == EventType.TASK_UPDATED));
    }

    @Test
    void testGetUserNotifications_NoNotifications() {
        // Arrange
        User user = createUser();
        entityManager.flush();
        entityManager.clear();

        // Act
        List<UserNotification> notifications = notificationService.getUserNotifications(user.getId());

        // Assert
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testGetTaskNotifications_ReturnsNotifications() {
        // Arrange
        User user = createUser();
        Long taskId = 100L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("task_id", taskId);
        UserNotification notification = createNotification(
                user.getId(), EventType.TASK_CREATED, "Task created", metadata
        );
        userNotificationRepository.save(notification);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserId(user.getId())
                .stream()
                .filter(n -> n.getMetadata().get("task_id") != null &&
                        taskId.longValue() == ((Number) n.getMetadata().get("task_id")).longValue())
                .toList();

        // Assert
        assertEquals(1, notifications.size());
        assertEquals(taskId.longValue(), ((Number) notifications.get(0).getMetadata().get("task_id")).longValue());
    }

    @Test
    void testGetTaskNotifications_NoNotifications() {
        // Arrange
        User user = createUser();
        Long taskId = 100L;
        entityManager.flush();
        entityManager.clear();

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserId(user.getId())
                .stream()
                .filter(n -> n.getMetadata().get("task_id") != null &&
                        taskId.longValue() == ((Number) n.getMetadata().get("task_id")).longValue())
                .toList();

        // Assert
        assertTrue(notifications.isEmpty());
    }
}