package com.taskasync.taskservice.repository;

import com.taskasync.taskservice.dto.ChangeDetails;
import com.taskasync.taskservice.dto.Priority; // Assuming Priority is in dto package
import com.taskasync.taskservice.dto.Status;   // Assuming Status is in dto package
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// Added properties to disable cloud features for isolated testing
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class TaskHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    // Helper method to create and persist a valid Task
    private Task createAndPersistValidTask(String title, Long creatorId) {
        Task task = new Task();
        task.setTitle(title);
        task.setCreatorId(creatorId);

        Map<Long, String> assignedUserMap = new HashMap<>();
        assignedUserMap.put(99L, "reviewer");
        task.setAssignedUsers(assignedUserMap);

        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.TODO);
        task.setCreatedAt(LocalDateTime.now());

        return entityManager.persistAndFlush(task);
    }


    @Test
    void testFindByTaskId_Success() {
        // Arrange
        Task persistedTask = createAndPersistValidTask("Test Task", 1L);
        Long taskId = persistedTask.getId();

        TaskHistory history = new TaskHistory();
        history.setTask(persistedTask);
        history.setChangedByUserId(1L);
        List<ChangeDetails.Change> changes = new ArrayList<>();
        changes.add(new ChangeDetails.Change("title", "Old Title", "Test Task"));
        history.setChangeDetails(new ChangeDetails(changes));
        TaskHistory savedHistory = entityManager.persistAndFlush(history); // Persist history and get ID

        // Act
        List<TaskHistoryDto> result = taskHistoryRepository.findByTaskId(taskId);

        // Assert
        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size());
        TaskHistoryDto dto = result.get(0);
        assertEquals(savedHistory.getId(), dto.getId(), "DTO ID should match saved history ID");
        assertEquals(taskId, dto.getTaskId());
        assertEquals(1L, dto.getChangedByUserId());
        assertNotNull(dto.getChangeDetails(), "ChangeDetails should not be null");
        assertEquals(1, dto.getChangeDetails().getChanges().size());
        assertEquals("title", dto.getChangeDetails().getChanges().get(0).getField());
    }

    @Test
    void testFindByTaskId_NoResults() {
        // Act
        List<TaskHistoryDto> result = taskHistoryRepository.findByTaskId(999L); // Use an ID that won't exist

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteByTaskId_Success() {
        // Arrange
        Task persistedTask = createAndPersistValidTask("Test Task To Delete", 1L);
        Long taskId = persistedTask.getId();

        TaskHistory history1 = new TaskHistory();
        history1.setTask(persistedTask);
        history1.setChangedByUserId(1L);
        history1.setChangeDetails(new ChangeDetails(new ArrayList<>()));
        entityManager.persist(history1);

        TaskHistory history2 = new TaskHistory();
        history2.setTask(persistedTask);
        history2.setChangedByUserId(2L);
        history2.setChangeDetails(new ChangeDetails(new ArrayList<>()));
        entityManager.persistAndFlush(history2); // Ensure they are in DB

        assertFalse(taskHistoryRepository.findByTaskId(taskId).isEmpty(), "History should exist before delete");

        // Act
        taskHistoryRepository.deleteByTaskId(taskId); // Perform the bulk deletion

        // --- FIX: Clear the persistence context BEFORE the next flush ---
        entityManager.clear();

        // Optional: Flush might not even be strictly necessary after clear.
        entityManager.flush();

        // Assert
        List<TaskHistoryDto> result = taskHistoryRepository.findByTaskId(taskId);
        assertTrue(result.isEmpty(), "History should be empty after deletion by task ID");
    }

    @Test
    void testSaveAndFindById() {
        // Arrange
        Task persistedTask = createAndPersistValidTask("Test Task For History", 1L);
        Long taskId = persistedTask.getId();

        TaskHistory history = new TaskHistory();
        history.setTask(persistedTask);
        history.setChangedByUserId(1L);
        history.setChangeDetails(new ChangeDetails(new ArrayList<>()));

        // Act
        TaskHistory savedHistory = taskHistoryRepository.save(history);
        entityManager.flush();
        entityManager.clear();

        Optional<TaskHistory> foundHistoryOptional = taskHistoryRepository.findById(savedHistory.getId());

        // Assert
        assertTrue(foundHistoryOptional.isPresent(), "Should find saved history by ID");
        TaskHistory foundHistory = foundHistoryOptional.get();
        assertNotNull(foundHistory.getId(), "Found history should have an ID");
        assertNotNull(foundHistory.getTask(), "Found history should have associated task");
        assertEquals(taskId, foundHistory.getTask().getId());
        assertEquals(1L, foundHistory.getChangedByUserId());
        assertNotNull(foundHistory.getChangeDetails(), "Found history change details should not be null");
        assertTrue(foundHistory.getChangeDetails().getChanges().isEmpty(), "Changes should be empty as saved");
    }
}