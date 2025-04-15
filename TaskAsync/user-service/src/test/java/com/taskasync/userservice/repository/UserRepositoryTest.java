package com.taskasync.userservice.repository;

import com.taskasync.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;


    private User createUser(String keycloakSubjectId, String email, String username) {
        User user = new User();
        user.setKeycloakSubjectId(keycloakSubjectId);
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    @Test
    void testFindByKeycloakSubjectId_Exists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        Optional<User> found = userRepository.findByKeycloakSubjectId("keycloak-123");

        
        assertTrue(found.isPresent());
        assertEquals("keycloak-123", found.get().getKeycloakSubjectId());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByKeycloakSubjectId_NotExists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        Optional<User> found = userRepository.findByKeycloakSubjectId("keycloak-999");

        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail_Exists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        Optional<User> found = userRepository.findByEmail("test@example.com");

        
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_NotExists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        Optional<User> found = userRepository.findByEmail("other@example.com");

        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsername_Exists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        Optional<User> found = userRepository.findByUsername("testuser");

        
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByUsername_NotExists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        Optional<User> found = userRepository.findByUsername("otheruser");

        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByKeycloakSubjectId_Exists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        boolean exists = userRepository.existsByKeycloakSubjectId("keycloak-123");

        
        assertTrue(exists);
    }

    @Test
    void testExistsByKeycloakSubjectId_NotExists() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");
        entityManager.persistAndFlush(user);

        
        boolean exists = userRepository.existsByKeycloakSubjectId("keycloak-999");

        
        assertFalse(exists);
    }

    @Test
    void testSaveAndFindById() {
        
        User user = createUser("keycloak-123", "test@example.com", "testuser");

        
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        
        assertTrue(foundUser.isPresent());
        User found = foundUser.get();
        assertNotNull(found.getId());
        assertEquals("keycloak-123", found.getKeycloakSubjectId());
        assertEquals("test@example.com", found.getEmail());
        assertEquals("testuser", found.getUsername());
        assertEquals("Test", found.getFirstName());
        assertEquals("User", found.getLastName());
    }

    @Test
    void testSave_ThrowsException_WhenRequiredFieldIsNull() {
        
        User user = new User();
        

        
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            entityManager.flush();
        }, "Should throw exception due to missing required fields");
    }
}