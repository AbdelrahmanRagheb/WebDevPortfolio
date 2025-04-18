package com.taskasync.userservice.repository;

import com.taskasync.userservice.dto.AssignmentStatus;
import com.taskasync.userservice.entity.User;
import com.taskasync.userservice.entity.UserTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class UserTaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserTaskRepository userTaskRepository;

    /**
     * Helper method to create a UserTask with a persisted User.
     */
    private UserTask createUserTask(Long taskId) {
        
        User user = new User();
        user.setKeycloakSubjectId(UUID.randomUUID().toString());
        user.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTaskId(taskId);
        userTask.setAssignmentStatus(AssignmentStatus.TODO);
        return userTask;
    }

    @Test
    void testFindByUserIdAndTaskId_Exists() {
        
        UserTask userTask = createUserTask(100L);
        Long userId = userTask.getUser().getId();
        userTaskRepository.saveAndFlush(userTask); 
        entityManager.clear();

        
        Optional<UserTask> found = userTaskRepository.findByUserIdAndTaskId(userId, 100L);

        
        assertTrue(found.isPresent(), "UserTask should be found");
        assertEquals(userId, found.get().getUser().getId());
        assertEquals(100L, found.get().getTaskId());
    }

    @Test
    void testFindByUserIdAndTaskId_NotExists() {
        
        UserTask userTask = createUserTask(100L);
        Long userId = userTask.getUser().getId();
        userTaskRepository.saveAndFlush(userTask);
        entityManager.clear();

        
        Optional<UserTask> found = userTaskRepository.findByUserIdAndTaskId(userId + 1, 100L);

        
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByUserIdAndTaskId_Exists() {
        
        UserTask userTask = createUserTask(100L);
        Long userId = userTask.getUser().getId();
        userTaskRepository.saveAndFlush(userTask);
        entityManager.clear();

        
        userTaskRepository.deleteByUserIdAndTaskId(userId, 100L);
        entityManager.flush();
        entityManager.clear();

        
        Optional<UserTask> found = userTaskRepository.findByUserIdAndTaskId(userId, 100L);
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteByUserIdAndTaskId_NotExists() {
        
        UserTask userTask = createUserTask(100L);
        Long userId = userTask.getUser().getId();
        userTaskRepository.saveAndFlush(userTask);
        entityManager.clear();

        
        userTaskRepository.deleteByUserIdAndTaskId(userId + 1, 100L);
        entityManager.flush();
        entityManager.clear();

        
        Optional<UserTask> found = userTaskRepository.findByUserIdAndTaskId(userId, 100L);
        assertTrue(found.isPresent());
    }

    @Test
    void testSaveAndFindById() {
        
        UserTask userTask = createUserTask(100L);
        Long userId = userTask.getUser().getId();

        
        UserTask savedUserTask = userTaskRepository.save(userTask);
        entityManager.flush();
        entityManager.clear();

        Optional<UserTask> foundUserTask = userTaskRepository.findById(savedUserTask.getId());

        
        assertTrue(foundUserTask.isPresent());
        UserTask found = foundUserTask.get();
        assertNotNull(found.getId());
        assertEquals(userId, found.getUser().getId());
        assertEquals(100L, found.getTaskId());
    }

    @Test
    void testSave_ThrowsException_WhenRequiredFieldIsNull() {
        
        UserTask userTask = new UserTask();
        

        
        assertThrows(Exception.class, () -> {
            userTaskRepository.save(userTask);
            entityManager.flush();
        }, "Should throw exception due to missing required fields");
    }
}