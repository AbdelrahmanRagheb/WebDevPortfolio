package com.taskasync.taskservice.repository;

import com.taskasync.taskservice.dto.Category;
import com.taskasync.taskservice.dto.Priority;
import com.taskasync.taskservice.dto.Status;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskComment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskCommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Test
    void testSaveAndFindById() {
        
        Task task = new Task();
        task.setTitle("Test Task");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); 
        task.setCategory(Category.WORK);   
        task.setPriority(Priority.MEDIUM); 
        task.setStatus(Status.TODO);       
        HashMap<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(99L, "reviewer");     
        task.setAssignedUsers(assignedUsers);
        entityManager.persist(task);

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setContent("Test Comment");
        comment.setCommenterId(1L);             
        comment.setCreatedAt(LocalDateTime.now()); 

        
        TaskComment savedComment = taskCommentRepository.save(comment);
        Optional<TaskComment> foundComment = taskCommentRepository.findById(savedComment.getId());

        
        assertTrue(foundComment.isPresent());
        assertEquals("Test Comment", foundComment.get().getContent());
        assertEquals(task.getId(), foundComment.get().getTask().getId());
    }

    @Test
    void testDeleteById() {
        
        Task task = new Task();
        task.setTitle("Test Task");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); 
        task.setCategory(Category.WORK);   
        task.setPriority(Priority.MEDIUM); 
        task.setStatus(Status.TODO);       
        HashMap<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(99L, "reviewer");     
        task.setAssignedUsers(assignedUsers);
        entityManager.persist(task);

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setContent("Test Comment");
        comment.setCommenterId(1L);             
        comment.setCreatedAt(LocalDateTime.now()); 
        TaskComment savedComment = entityManager.persistAndFlush(comment);

        
        taskCommentRepository.deleteById(savedComment.getId());
        Optional<TaskComment> foundComment = taskCommentRepository.findById(savedComment.getId());

        
        assertFalse(foundComment.isPresent());
    }
}