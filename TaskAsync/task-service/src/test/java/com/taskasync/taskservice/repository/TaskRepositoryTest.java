package com.taskasync.taskservice.repository;

// --- Make sure these imports match your actual enum location/names ---
import com.taskasync.taskservice.dto.Priority;
import com.taskasync.taskservice.dto.Status;
// --- End Enum Imports ---

import com.taskasync.taskservice.entity.Task;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap; // Import HashMap
import java.util.Map;     // Import Map
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Helper method to create a Task instance with all required fields initialized
     * to satisfy potential validation constraints (@NotNull, @NotEmpty etc.).
     */
    private Task createTask(String title, Long creatorId) {
        Task task = new Task();
        task.setTitle(title);
        task.setCreatorId(creatorId);

        // Initialize assignedUsers (assuming Map<Long, String> based on usage)
        // The previous error "cannot be empty if provided" suggests an empty map might be invalid.
        Map<Long, String> assignedUserMap = new HashMap<>();
        assignedUserMap.put(99L, "reviewer"); // Dummy user ID 99L with role "reviewer"
        task.setAssignedUsers(assignedUserMap);

        // Initialize other fields marked as NOT NULL in the schema or entity
        task.setPriority(Priority.MEDIUM); // Ensure Priority enum is correctly imported/named
        task.setStatus(Status.TODO);       // Ensure Status enum is correctly imported/named
        task.setCreatedAt(LocalDateTime.now());

        return task;
    }

    @Test
    void testExistsByTitleAndCreatorId_Exists() {
        // Arrange
        Task task = createTask("Test Task", 1L);
        entityManager.persistAndFlush(task);

        // Act
        boolean exists = taskRepository.existsByTitleAndCreatorId("Test Task", 1L);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByTitleAndCreatorId_NotExists() {
        // Arrange
        Task task = createTask("Test Task", 1L);
        entityManager.persistAndFlush(task);

        // Act
        boolean exists = taskRepository.existsByTitleAndCreatorId("Other Task", 1L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testExistsByTitleAndCreatorId_DifferentCreator() {
        // Arrange
        Task task = createTask("Test Task", 1L);
        entityManager.persistAndFlush(task);

        // Act
        boolean exists = taskRepository.existsByTitleAndCreatorId("Test Task", 2L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testSaveAndFindById() {
        // Arrange
        Task task = createTask("Test Task", 1L);

        // Act
        Task savedTask = taskRepository.save(task);
        entityManager.flush();
        entityManager.clear();

        Optional<Task> foundTaskOptional = taskRepository.findById(savedTask.getId());

        // Assert
        assertTrue(foundTaskOptional.isPresent());
        Task foundTask = foundTaskOptional.get();
        assertNotNull(foundTask.getId());
        assertEquals("Test Task", foundTask.getTitle());
        assertEquals(1L, foundTask.getCreatorId());
        assertNotNull(foundTask.getAssignedUsers());
        assertFalse(foundTask.getAssignedUsers().isEmpty());
        assertEquals(1, foundTask.getAssignedUsers().size());

        // --- FIX: Assert map content correctly ---
        assertTrue(foundTask.getAssignedUsers().containsKey(99L), "Map should contain key 99L");
        assertEquals("reviewer", foundTask.getAssignedUsers().get(99L), "Value for key 99L should be 'reviewer'");
        // --- End Fix ---

        assertEquals(Priority.MEDIUM, foundTask.getPriority());
        assertEquals(Status.TODO, foundTask.getStatus());
        assertNotNull(foundTask.getCreatedAt());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Task task = createTask("Test Task To Delete", 1L);
        Task savedTask = entityManager.persistAndFlush(task);
        Long taskId = savedTask.getId();

        assertTrue(taskRepository.findById(taskId).isPresent());

        // Act
        taskRepository.deleteById(taskId);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Task> foundTask = taskRepository.findById(taskId);
        assertFalse(foundTask.isPresent());
    }

    @Test
    void testSave_ThrowsException_WhenRequiredFieldIsNull() {
        // Arrange
        Task task = new Task(); // Intentionally incomplete task
        task.setCreatorId(1L);
        // Initialize assignedUsers as it's required to not be empty when provided
        Map<Long, String> assignedUserMap = new HashMap<>();
        assignedUserMap.put(99L, "reviewer");
        task.setAssignedUsers(assignedUserMap);
        // Missing title, priority, status etc.

        // Act & Assert
        // Expect ConstraintViolationException as validation happens before DB constraints
        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(task);
        }, "Should throw ConstraintViolationException due to missing required fields");
    }
}