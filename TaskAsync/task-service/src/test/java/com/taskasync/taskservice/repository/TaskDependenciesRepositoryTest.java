package com.taskasync.taskservice.repository;

import com.taskasync.taskservice.dto.Category;
import com.taskasync.taskservice.dto.Priority;
import com.taskasync.taskservice.dto.Status;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskDependency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskDependenciesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskDependenciesRepository taskDependenciesRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Task task = new Task();
        task.setTitle("Task 1");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); // Set required createdAt field
        task.setCategory(Category.WORK);   // Set required category field
        task.setPriority(Priority.MEDIUM); // Set required priority field
        task.setStatus(Status.TODO);       // Set required status field
        HashMap<Long, String> assignedUsers1 = new HashMap<>();
        assignedUsers1.put(99L, "reviewer");
        task.setAssignedUsers(assignedUsers1);
        entityManager.persist(task);

        Task dependsOnTask = new Task();
        dependsOnTask.setTitle("Task 2");
        dependsOnTask.setCreatorId(1L);
        dependsOnTask.setCreatedAt(LocalDateTime.now()); // Set required createdAt field
        dependsOnTask.setCategory(Category.WORK);   // Set required category field
        dependsOnTask.setPriority(Priority.MEDIUM); // Set required priority field
        dependsOnTask.setStatus(Status.TODO);       // Set required status field
        HashMap<Long, String> assignedUsers2 = new HashMap<>();
        assignedUsers2.put(99L, "reviewer");
        dependsOnTask.setAssignedUsers(assignedUsers2);
        entityManager.persist(dependsOnTask);

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        dependency.setCreatedAt(LocalDateTime.now()); // Set required createdAt for TaskDependency

        // Act
        TaskDependency savedDependency = taskDependenciesRepository.save(dependency);
        Optional<TaskDependency> foundDependency = taskDependenciesRepository.findById(savedDependency.getId());

        // Assert
        assertTrue(foundDependency.isPresent());
        assertEquals(task.getId(), foundDependency.get().getTask().getId());
        assertEquals(dependsOnTask.getId(), foundDependency.get().getDependsOnTask().getId());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Task task = new Task();
        task.setTitle("Task 1");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); // Set required createdAt field
        task.setCategory(Category.WORK);   // Set required category field
        task.setPriority(Priority.MEDIUM); // Set required priority field
        task.setStatus(Status.TODO);       // Set required status field
        HashMap<Long, String> assignedUsers1 = new HashMap<>();
        assignedUsers1.put(99L, "reviewer");
        task.setAssignedUsers(assignedUsers1);
        entityManager.persist(task);

        Task dependsOnTask = new Task();
        dependsOnTask.setTitle("Task 2");
        dependsOnTask.setCreatorId(1L);
        dependsOnTask.setCreatedAt(LocalDateTime.now()); // Set required createdAt field
        dependsOnTask.setCategory(Category.WORK);   // Set required category field
        dependsOnTask.setPriority(Priority.MEDIUM); // Set required priority field
        dependsOnTask.setStatus(Status.TODO);       // Set required status field
        HashMap<Long, String> assignedUsers2 = new HashMap<>();
        assignedUsers2.put(99L, "reviewer");
        dependsOnTask.setAssignedUsers(assignedUsers2); // Fixed: Set assignedUsers for dependsOnTask
        entityManager.persist(dependsOnTask);

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        dependency.setCreatedAt(LocalDateTime.now()); // Set required createdAt for TaskDependency
        TaskDependency savedDependency = entityManager.persistAndFlush(dependency);

        // Act
        taskDependenciesRepository.deleteById(savedDependency.getId());
        Optional<TaskDependency> foundDependency = taskDependenciesRepository.findById(savedDependency.getId());

        // Assert
        assertFalse(foundDependency.isPresent());
    }
}