package com.taskasync.userservice.entity;

import com.taskasync.userservice.dto.AssignmentStatus;
import com.taskasync.userservice.dto.notification.EventType;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EntityValidationAndPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        entityManager.clear(); // Ensure clean database before each test
    }

    // --- User Entity Tests ---
    @Test
    void testUser_ValidEntity_ShouldPassValidation() {
        User user = new User();
        user.setKeycloakSubjectId("keycloak-123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setFullName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid User should have no validation errors");
    }

    @Test
    void testUser_NullKeycloakSubjectId_ShouldFailValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setFullName("Test User");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Keycloak Subject ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testUserNotification_Persist_ShouldSetAuditFields() {
        // Arrange
        User user = new User();
        user.setKeycloakSubjectId("keycloak-123");
        user.setEmail("test@example.com");
        user.setUsername("user");
        user = entityManager.persistAndFlush(user); // Ensure User is persisted

        UserNotification notification = new UserNotification();
        notification.setUserId(user.getId());
        notification.setMessage("Test notification");
        notification.setNotificationType(EventType.TASK_COMMENT_ADDED);

        // Act
        UserNotification persistedNotification = entityManager.persistFlushFind(notification);

        // Assert
        assertNotNull(persistedNotification.getCreatedAt(), "CreatedAt should be set after persistence");
        assertEquals(user.getId(), persistedNotification.getUserId(), "User ID should match");
    }

    @Test
    void testUser_UniqueConstraints_ShouldPreventDuplicates() {
        // Arrange
        User user1 = new User();
        user1.setKeycloakSubjectId("keycloak-123");
        user1.setEmail("test1@example.com");
        user1.setUsername("user1");
        entityManager.persistAndFlush(user1); // Ensure first user is persisted

        User user2 = new User();
        user2.setKeycloakSubjectId("keycloak-123"); // Duplicate keycloakSubjectId
        user2.setEmail("test2@example.com");
        user2.setUsername("user2");

        // Act & Assert
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(user2);
        }, "Should throw exception due to duplicate keycloakSubjectId");
    }

    // --- UserNotification Entity Tests ---
    @Test
    void testUserNotification_ValidEntity_ShouldPassValidation() {
        UserNotification notification = new UserNotification();
        notification.setUserId(1L);
        notification.setNotificationType(EventType.TASK_ROLE_ADDED);
        notification.setMessage("Task assigned to you");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("taskId", 1L);
        notification.setMetadata(metadata);

        Set<ConstraintViolation<UserNotification>> violations = validator.validate(notification);
        assertTrue(violations.isEmpty(), "Valid UserNotification should have no validation errors");
    }

    @Test
    void testUserNotification_NullMessage_ShouldFailValidation() {
        UserNotification notification = new UserNotification();
        notification.setUserId(1L);
        notification.setNotificationType(EventType.TASK_ROLE_ADDED);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("taskId", 1L);
        notification.setMetadata(metadata);
        notification.setMessage(null); // Explicitly set to null

        Set<ConstraintViolation<UserNotification>> violations = validator.validate(notification);
        assertEquals(1, violations.size());
        assertEquals("Message cannot be blank", violations.iterator().next().getMessage());
    }




    // --- UserTask Entity Tests ---
    @Test
    void testUserTask_ValidEntity_ShouldPassValidation() {
        User user = new User();
        user.setKeycloakSubjectId("keycloak-123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTaskId(1L);
        userTask.setAssignedAt(LocalDateTime.now());
        userTask.setUpdatedAt(LocalDateTime.now());
        userTask.setAssignmentStatus(AssignmentStatus.TODO);
        userTask.setRoleInTask("DEVELOPER");

        Set<ConstraintViolation<UserTask>> violations = validator.validate(userTask);
        assertTrue(violations.isEmpty(), "Valid UserTask should have no validation errors");
    }

    @Test
    void testUserTask_NullUser_ShouldFailValidation() {
        UserTask userTask = new UserTask();
        userTask.setTaskId(1L);
        userTask.setAssignedAt(LocalDateTime.now());
        userTask.setUpdatedAt(LocalDateTime.now());
        userTask.setAssignmentStatus(AssignmentStatus.TODO);
        userTask.setRoleInTask("DEVELOPER");

        Set<ConstraintViolation<UserTask>> violations = validator.validate(userTask);
        assertEquals(1, violations.size(), "Expected one validation error for null user");
        assertEquals("User cannot be null", violations.iterator().next().getMessage());
    }


    @Test
    void testUserTask_PersistWithRelationship_ShouldSetFields() {
        User user = new User();
        user.setKeycloakSubjectId("keycloak-123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTaskId(1L);
        userTask.setAssignedAt(LocalDateTime.now());
        userTask.setUpdatedAt(LocalDateTime.now());
        userTask.setAssignmentStatus(AssignmentStatus.TODO);
        userTask.setRoleInTask("DEVELOPER");

        UserTask persistedUserTask = entityManager.persistFlushFind(userTask);

        assertNotNull(persistedUserTask.getId());
        assertNotNull(persistedUserTask.getUser());
        assertEquals(user.getId(), persistedUserTask.getUser().getId());
        assertEquals(1L, persistedUserTask.getTaskId());
        assertEquals(AssignmentStatus.TODO, persistedUserTask.getAssignmentStatus());
        assertEquals("DEVELOPER", persistedUserTask.getRoleInTask());
    }
}