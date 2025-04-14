package com.taskasync.userservice.repository;

import com.taskasync.userservice.dto.notification.EventType;
import com.taskasync.userservice.entity.UserNotification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class UserNotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    /**
     * Helper method to create a UserNotification instance with required fields.
     */
    private UserNotification createUserNotification(Long userId, EventType notificationType, Long taskId) {
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setNotificationType(notificationType);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setMessage("Notification for " + notificationType.name().toLowerCase() + " event"); // Set required message field

        // Set metadata with task_id for JSON_EXTRACT query
        Map<String, Object> metadata = new HashMap<>();
        if (taskId != null) {
            metadata.put("task_id", taskId);
        }
        notification.setMetadata(metadata);

        return notification;
    }

    @Test
    void testFindByUserId_Exists() {
        // Arrange
        UserNotification notification1 = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);
        UserNotification notification2 = createUserNotification(1L, EventType.TASK_UPDATED, 101L);
        entityManager.persist(notification1);
        entityManager.persist(notification2);
        entityManager.flush();

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserId(1L);

        // Assert
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getNotificationType().equals(EventType.TASK_ROLE_ADDED)));
        assertTrue(notifications.stream().anyMatch(n -> n.getNotificationType().equals(EventType.TASK_UPDATED)));
    }

    @Test
    void testFindByUserId_NotExists() {
        // Arrange
        UserNotification notification = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);
        entityManager.persistAndFlush(notification);

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserId(2L);

        // Assert
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testFindByUserIdAndTaskId_Exists() {
        // Arrange
        UserNotification notification = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);
        entityManager.persistAndFlush(notification);

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserIdAndTaskId(1L, 100L);

        // Assert
        assertEquals(1, notifications.size());
        assertEquals(EventType.TASK_ROLE_ADDED, notifications.getFirst().getNotificationType());
        assertEquals(100L, notifications.getFirst().getMetadata().get("task_id"));
    }

    @Test
    void testFindByUserIdAndTaskId_NotExists() {
        // Arrange
        UserNotification notification = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);
        entityManager.persistAndFlush(notification);

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserIdAndTaskId(1L, 999L);

        // Assert
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testFindByUserIdAndType_Exists() {
        // Arrange
        UserNotification notification1 = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);
        UserNotification notification2 = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 101L);
        UserNotification notification3 = createUserNotification(1L, EventType.TASK_UPDATED, 102L); // Different type
        entityManager.persist(notification1);
        entityManager.persist(notification2);
        entityManager.persist(notification3);
        entityManager.flush();

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserIdAndType(1L, EventType.TASK_ROLE_ADDED);

        // Assert
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> n.getNotificationType().equals(EventType.TASK_ROLE_ADDED)));
    }

    @Test
    void testFindByUserIdAndType_NotExists() {
        // Arrange
        UserNotification notification = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);
        entityManager.persistAndFlush(notification);

        // Act
        List<UserNotification> notifications = userNotificationRepository.findByUserIdAndType(1L, EventType.TASK_DELETED);

        // Assert
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testSaveAndFindById() {
        // Arrange
        UserNotification notification = createUserNotification(1L, EventType.TASK_ROLE_ADDED, 100L);

        // Act
        UserNotification savedNotification = userNotificationRepository.save(notification);
        entityManager.flush();
        entityManager.clear();

        Optional<UserNotification> foundNotification = userNotificationRepository.findById(savedNotification.getId());

        // Assert
        assertTrue(foundNotification.isPresent());
        UserNotification found = foundNotification.get();
        assertNotNull(found.getId());
        assertEquals(1L, found.getUserId());
        assertEquals(EventType.TASK_ROLE_ADDED, found.getNotificationType());
        assertEquals(100, found.getMetadata().get("task_id"));
        assertNotNull(found.getMessage());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void testSave_ThrowsException_WhenRequiredFieldIsNull() {
        // Arrange
        UserNotification notification = new UserNotification();
        // Missing userId, notificationType, createdAt, message

        // Act & Assert
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
            userNotificationRepository.save(notification);
            entityManager.flush();
        }, "Should throw ConstraintViolationException due to missing required fields");
    }
}