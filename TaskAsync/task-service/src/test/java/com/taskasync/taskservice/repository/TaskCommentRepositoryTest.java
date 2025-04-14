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
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); // Required: created_at NOT NULL
        task.setCategory(Category.WORK);   // Optional, but setting for completeness
        task.setPriority(Priority.MEDIUM); // Required: priority NOT NULL
        task.setStatus(Status.TODO);       // Required: status NOT NULL
        HashMap<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(99L, "reviewer");     // Required: assigned_users NOT NULL and must be non-empty
        task.setAssignedUsers(assignedUsers);
        entityManager.persist(task);

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setContent("Test Comment");
        comment.setCommenterId(1L);             // Required: commenter_id NOT NULL
        comment.setCreatedAt(LocalDateTime.now()); // Required: created_at NOT NULL

        // Act
        TaskComment savedComment = taskCommentRepository.save(comment);
        Optional<TaskComment> foundComment = taskCommentRepository.findById(savedComment.getId());

        // Assert
        assertTrue(foundComment.isPresent());
        assertEquals("Test Comment", foundComment.get().getContent());
        assertEquals(task.getId(), foundComment.get().getTask().getId());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setCreatorId(1L);
        task.setCreatedAt(LocalDateTime.now()); // Required: created_at NOT NULL
        task.setCategory(Category.WORK);   // Optional, but setting for completeness
        task.setPriority(Priority.MEDIUM); // Required: priority NOT NULL
        task.setStatus(Status.TODO);       // Required: status NOT NULL
        HashMap<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(99L, "reviewer");     // Required: assigned_users NOT NULL and must be non-empty
        task.setAssignedUsers(assignedUsers);
        entityManager.persist(task);

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setContent("Test Comment");
        comment.setCommenterId(1L);             // Required: commenter_id NOT NULL
        comment.setCreatedAt(LocalDateTime.now()); // Required: created_at NOT NULL
        TaskComment savedComment = entityManager.persistAndFlush(comment);

        // Act
        taskCommentRepository.deleteById(savedComment.getId());
        Optional<TaskComment> foundComment = taskCommentRepository.findById(savedComment.getId());

        // Assert
        assertFalse(foundComment.isPresent());
    }
}