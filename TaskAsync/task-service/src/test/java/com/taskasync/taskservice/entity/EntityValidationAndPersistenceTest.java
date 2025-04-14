package com.taskasync.taskservice.entity;

import com.taskasync.taskservice.dto.Category;
import com.taskasync.taskservice.dto.ChangeDetails;
import com.taskasync.taskservice.dto.Priority;
import com.taskasync.taskservice.dto.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
        // Ensure the database is clean before each test
        entityManager.clear();
    }

    // --- Task Entity Tests ---
    @Test
    void testTask_ValidEntity_ShouldPassValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Valid Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertTrue(violations.isEmpty(), "Valid Task should have no validation errors");
    }

    @Test
    void testTask_NullCreatorId_ShouldFailValidation() {
        Task task = new Task();
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertEquals(1, violations.size());
        assertEquals("Creator ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testTask_EmptyAssignedUsers_ShouldFailValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        task.setAssignedUsers(new HashMap<>()); // Empty map

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertEquals(1, violations.size());
        assertEquals("Assigned users cannot be empty if provided", violations.iterator().next().getMessage());
    }

    @Test
    void testTask_PastDueDate_ShouldFailValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);
        task.setDueDate(LocalDateTime.now().minusDays(1)); // Past date

        Set<ConstraintViolation<Task>> violations = validator.validate(task);
        assertEquals(1, violations.size());
        assertEquals("Due date must be in the present or future", violations.iterator().next().getMessage());
    }

    @Test
    void testTask_PersistWithRelationships_ShouldSetAuditFields() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        TaskComment comment = new TaskComment();
        comment.setCommenterId(1L);
        comment.setContent("Test Comment");
        comment.setTask(task);
        task.getComments().add(comment);

        Task persistedTask = entityManager.persistFlushFind(task);

        assertNotNull(persistedTask.getId());
        assertNotNull(persistedTask.getCreatedAt());
        assertNull(persistedTask.getUpdatedAt()); // updatedAt is set on update, not insert
        assertEquals(1, persistedTask.getComments().size());
        assertEquals("Test Comment", persistedTask.getComments().getFirst().getContent());
    }

    // --- TaskComment Entity Tests ---
    @Test
    void testTaskComment_ValidEntity_ShouldPassValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        TaskComment comment = new TaskComment();
        comment.setCommenterId(1L);
        comment.setContent("Valid Comment");
        comment.setTask(task);

        Set<ConstraintViolation<TaskComment>> violations = validator.validate(comment);
        assertTrue(violations.isEmpty(), "Valid TaskComment should have no validation errors");
    }

    @Test
    void testTaskComment_NullContent_ShouldFailValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        TaskComment comment = new TaskComment();
        comment.setCommenterId(1L);
        comment.setTask(task);

        Set<ConstraintViolation<TaskComment>> violations = validator.validate(comment);
        assertEquals(1, violations.size());
        assertEquals("Content cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testTaskComment_PersistWithParentComment_ShouldSetAuditFields() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);
        entityManager.persist(task);

        TaskComment parentComment = new TaskComment();
        parentComment.setCommenterId(1L);
        parentComment.setContent("Parent Comment");
        parentComment.setTask(task);
        entityManager.persist(parentComment);

        TaskComment childComment = new TaskComment();
        childComment.setCommenterId(1L);
        childComment.setContent("Child Comment");
        childComment.setTask(task);
        childComment.setParentComment(parentComment);

        TaskComment persistedComment = entityManager.persistFlushFind(childComment);

        assertNotNull(persistedComment.getId());
        assertNotNull(persistedComment.getCreatedAt());
        assertEquals(parentComment.getId(), persistedComment.getParentComment().getId());
    }

    // --- TaskDependency Entity Tests ---
    @Test
    void testTaskDependency_ValidEntity_ShouldPassValidation() {
        Task task1 = new Task();
        task1.setCreatorId(1L);
        task1.setTitle("Task 1");
        task1.setPriority(Priority.MEDIUM);
        task1.setStatus(Status.TODO);
        Map<Long, String> assignedUsers1 = new HashMap<>();
        assignedUsers1.put(1L, "reviewer");
        task1.setAssignedUsers(assignedUsers1);

        Task task2 = new Task();
        task2.setCreatorId(1L);
        task2.setTitle("Task 2");
        task2.setPriority(Priority.MEDIUM);
        task2.setStatus(Status.TODO);
        Map<Long, String> assignedUsers2 = new HashMap<>();
        assignedUsers2.put(1L, "reviewer");
        task2.setAssignedUsers(assignedUsers2);

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task1);
        dependency.setDependsOnTask(task2);

        Set<ConstraintViolation<TaskDependency>> violations = validator.validate(dependency);
        assertTrue(violations.isEmpty(), "Valid TaskDependency should have no validation errors");
    }

    @Test
    void testTaskDependency_NullTask_ShouldFailValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        TaskDependency dependency = new TaskDependency();
        dependency.setDependsOnTask(task);

        Set<ConstraintViolation<TaskDependency>> violations = validator.validate(dependency);
        assertEquals(1, violations.size());
        assertEquals("Task ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testTaskDependency_Persist_ShouldSetAuditFields() {
        Task task1 = new Task();
        task1.setCreatorId(1L);
        task1.setTitle("Task 1");
        task1.setPriority(Priority.MEDIUM);
        task1.setStatus(Status.TODO);
        Map<Long, String> assignedUsers1 = new HashMap<>();
        assignedUsers1.put(1L, "reviewer");
        task1.setAssignedUsers(assignedUsers1);
        entityManager.persist(task1);

        Task task2 = new Task();
        task2.setCreatorId(1L);
        task2.setTitle("Task 2");
        task2.setPriority(Priority.MEDIUM);
        task2.setStatus(Status.TODO);
        Map<Long, String> assignedUsers2 = new HashMap<>();
        assignedUsers2.put(1L, "reviewer");
        task2.setAssignedUsers(assignedUsers2);
        entityManager.persist(task2);

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task1);
        dependency.setDependsOnTask(task2);

        TaskDependency persistedDependency = entityManager.persistFlushFind(dependency);

        assertNotNull(persistedDependency.getId());
        assertNotNull(persistedDependency.getCreatedAt());
        assertEquals(task1.getId(), persistedDependency.getTask().getId());
        assertEquals(task2.getId(), persistedDependency.getDependsOnTask().getId());
    }

    // --- TaskHistory Entity Tests ---
    @Test
    void testTaskHistory_ValidEntity_ShouldPassValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setChangedByUserId(1L);
        history.setChangeDetails(new ChangeDetails()); // Adjust based on your ChangeDetails implementation

        Set<ConstraintViolation<TaskHistory>> violations = validator.validate(history);
        assertTrue(violations.isEmpty(), "Valid TaskHistory should have no validation errors");
    }

    @Test
    void testTaskHistory_NullChangeDetails_ShouldFailValidation() {
        Task task = new Task();
        task.setCreatorId(1L);
        task.setTitle("Task");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(1L, "reviewer");
        task.setAssignedUsers(assignedUsers);

        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setChangedByUserId(1L);

        Set<ConstraintViolation<TaskHistory>> violations = validator.validate(history);
        assertEquals(1, violations.size());
        assertEquals("Change details cannot be null", violations.iterator().next().getMessage());
    }


}