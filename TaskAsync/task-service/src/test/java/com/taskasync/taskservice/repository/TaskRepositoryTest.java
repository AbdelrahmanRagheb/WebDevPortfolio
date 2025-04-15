package com.taskasync.taskservice.repository;


import com.taskasync.taskservice.dto.Priority;
import com.taskasync.taskservice.dto.Status;


import com.taskasync.taskservice.entity.Task;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap; 
import java.util.Map;     
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

        
        
        Map<Long, String> assignedUserMap = new HashMap<>();
        assignedUserMap.put(99L, "reviewer"); 
        task.setAssignedUsers(assignedUserMap);

        
        task.setPriority(Priority.MEDIUM); 
        task.setStatus(Status.TODO);       
        task.setCreatedAt(LocalDateTime.now());

        return task;
    }

    @Test
    void testExistsByTitleAndCreatorId_Exists() {
        
        Task task = createTask("Test Task", 1L);
        entityManager.persistAndFlush(task);

        
        boolean exists = taskRepository.existsByTitleAndCreatorId("Test Task", 1L);

        
        assertTrue(exists);
    }

    @Test
    void testExistsByTitleAndCreatorId_NotExists() {
        
        Task task = createTask("Test Task", 1L);
        entityManager.persistAndFlush(task);

        
        boolean exists = taskRepository.existsByTitleAndCreatorId("Other Task", 1L);

        
        assertFalse(exists);
    }

    @Test
    void testExistsByTitleAndCreatorId_DifferentCreator() {
        
        Task task = createTask("Test Task", 1L);
        entityManager.persistAndFlush(task);

        
        boolean exists = taskRepository.existsByTitleAndCreatorId("Test Task", 2L);

        
        assertFalse(exists);
    }

    @Test
    void testSaveAndFindById() {
        
        Task task = createTask("Test Task", 1L);

        
        Task savedTask = taskRepository.save(task);
        entityManager.flush();
        entityManager.clear();

        Optional<Task> foundTaskOptional = taskRepository.findById(savedTask.getId());

        
        assertTrue(foundTaskOptional.isPresent());
        Task foundTask = foundTaskOptional.get();
        assertNotNull(foundTask.getId());
        assertEquals("Test Task", foundTask.getTitle());
        assertEquals(1L, foundTask.getCreatorId());
        assertNotNull(foundTask.getAssignedUsers());
        assertFalse(foundTask.getAssignedUsers().isEmpty());
        assertEquals(1, foundTask.getAssignedUsers().size());

        
        assertTrue(foundTask.getAssignedUsers().containsKey(99L), "Map should contain key 99L");
        assertEquals("reviewer", foundTask.getAssignedUsers().get(99L), "Value for key 99L should be 'reviewer'");
        

        assertEquals(Priority.MEDIUM, foundTask.getPriority());
        assertEquals(Status.TODO, foundTask.getStatus());
        assertNotNull(foundTask.getCreatedAt());
    }

    @Test
    void testDeleteById() {
        
        Task task = createTask("Test Task To Delete", 1L);
        Task savedTask = entityManager.persistAndFlush(task);
        Long taskId = savedTask.getId();

        assertTrue(taskRepository.findById(taskId).isPresent());

        
        taskRepository.deleteById(taskId);
        entityManager.flush();
        entityManager.clear();

        
        Optional<Task> foundTask = taskRepository.findById(taskId);
        assertFalse(foundTask.isPresent());
    }

    @Test
    void testSave_ThrowsException_WhenRequiredFieldIsNull() {
        
        Task task = new Task(); 
        task.setCreatorId(1L);
        
        Map<Long, String> assignedUserMap = new HashMap<>();
        assignedUserMap.put(99L, "reviewer");
        task.setAssignedUsers(assignedUserMap);
        

        
        
        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(task);
        }, "Should throw ConstraintViolationException due to missing required fields");
    }
}