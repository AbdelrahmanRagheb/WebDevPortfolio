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
        
        Task task = new Task();
        task.setTitle("Task 1");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); 
        task.setCategory(Category.WORK);   
        task.setPriority(Priority.MEDIUM); 
        task.setStatus(Status.TODO);       
        HashMap<Long, String> assignedUsers1 = new HashMap<>();
        assignedUsers1.put(99L, "reviewer");
        task.setAssignedUsers(assignedUsers1);
        entityManager.persist(task);

        Task dependsOnTask = new Task();
        dependsOnTask.setTitle("Task 2");
        dependsOnTask.setCreatorId(1L);
        dependsOnTask.setCreatedAt(LocalDateTime.now()); 
        dependsOnTask.setCategory(Category.WORK);   
        dependsOnTask.setPriority(Priority.MEDIUM); 
        dependsOnTask.setStatus(Status.TODO);       
        HashMap<Long, String> assignedUsers2 = new HashMap<>();
        assignedUsers2.put(99L, "reviewer");
        dependsOnTask.setAssignedUsers(assignedUsers2);
        entityManager.persist(dependsOnTask);

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        dependency.setCreatedAt(LocalDateTime.now()); 

        
        TaskDependency savedDependency = taskDependenciesRepository.save(dependency);
        Optional<TaskDependency> foundDependency = taskDependenciesRepository.findById(savedDependency.getId());

        
        assertTrue(foundDependency.isPresent());
        assertEquals(task.getId(), foundDependency.get().getTask().getId());
        assertEquals(dependsOnTask.getId(), foundDependency.get().getDependsOnTask().getId());
    }

    @Test
    void testDeleteById() {
        
        Task task = new Task();
        task.setTitle("Task 1");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); 
        task.setCategory(Category.WORK);   
        task.setPriority(Priority.MEDIUM); 
        task.setStatus(Status.TODO);       
        HashMap<Long, String> assignedUsers1 = new HashMap<>();
        assignedUsers1.put(99L, "reviewer");
        task.setAssignedUsers(assignedUsers1);
        entityManager.persist(task);

        Task dependsOnTask = new Task();
        dependsOnTask.setTitle("Task 2");
        dependsOnTask.setCreatorId(1L);
        dependsOnTask.setCreatedAt(LocalDateTime.now()); 
        dependsOnTask.setCategory(Category.WORK);   
        dependsOnTask.setPriority(Priority.MEDIUM); 
        dependsOnTask.setStatus(Status.TODO);       
        HashMap<Long, String> assignedUsers2 = new HashMap<>();
        assignedUsers2.put(99L, "reviewer");
        dependsOnTask.setAssignedUsers(assignedUsers2); 
        entityManager.persist(dependsOnTask);

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        dependency.setCreatedAt(LocalDateTime.now()); 
        TaskDependency savedDependency = entityManager.persistAndFlush(dependency);

        
        taskDependenciesRepository.deleteById(savedDependency.getId());
        Optional<TaskDependency> foundDependency = taskDependenciesRepository.findById(savedDependency.getId());

        
        assertFalse(foundDependency.isPresent());
    }
}