package com.taskasync.userservice.service.impl;

import com.taskasync.userservice.dto.UserDto;
import com.taskasync.userservice.entity.User;
import com.taskasync.userservice.mapper.UserMapper;
import com.taskasync.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
public class IUserServiceImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    @InjectMocks
    private IUserServiceImpl userService;

    /**
     * Helper method to create a User entity.
     */
    private User createUser(String keycloakId) {
        User user = new User();
        user.setKeycloakSubjectId(keycloakId);
        user.setCreatedAt(LocalDateTime.now());
        user.setEmail("test" + UUID.randomUUID() + "@example.com");
        user.setUsername("user" + UUID.randomUUID());
        return user;
    }

    @Test
    void testCreateNewUser_Success() {
        // Arrange
        UserDto userDto = new UserDto();
        User user = createUser(UUID.randomUUID().toString());
        when(userMapper.mapToUser(any(UserDto.class), any(User.class))).thenReturn(user);
        when(userMapper.mapToUserDto(any(User.class), any(UserDto.class))).thenReturn(userDto);

        // Act
        Long userId = userService.createNewUser(userDto);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<User> savedUser = userRepository.findById(userId);
        assertTrue(savedUser.isPresent());
        assertEquals(user.getKeycloakSubjectId(), savedUser.get().getKeycloakSubjectId());
    }

    @Test
    void testCheckUserExists_True() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();
        User user = createUser(keycloakId);
        entityManager.persistAndFlush(user);
        entityManager.clear();

        // Act
        boolean exists = userService.checkUserExists(keycloakId);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCheckUserExists_False() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();

        // Act
        boolean exists = userService.checkUserExists(keycloakId);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testGetUserIdByKeycloakId_Found() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();
        User user = createUser(keycloakId);
        entityManager.persistAndFlush(user);
        entityManager.clear();

        // Act
        Long userId = userService.getUserIdByKeycloakId(keycloakId);

        // Assert
        assertNotNull(userId);
        assertEquals(user.getId(), userId);
    }

    @Test
    void testGetUserIdByKeycloakId_NotFound() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();

        // Act
        Long userId = userService.getUserIdByKeycloakId(keycloakId);

        // Assert
        assertNull(userId);
    }

    @Test
    void testGetUserByKeycloakId_Found() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();
        User user = createUser(keycloakId);
        UserDto userDto = new UserDto();
        entityManager.persistAndFlush(user);
        when(userMapper.mapToUserDto(any(User.class), any(UserDto.class))).thenReturn(userDto);
        entityManager.clear();

        // Act
        UserDto result = userService.getUserByKeycloakId(keycloakId);

        // Assert
        assertNotNull(result);
        verify(userMapper).mapToUserDto(eq(user), any(UserDto.class));
    }

    @Test
    void testGetUserByKeycloakId_NotFound() {
        // Arrange
        String keycloakId = UUID.randomUUID().toString();

        // Act
        UserDto result = userService.getUserByKeycloakId(keycloakId);

        // Assert
        assertNull(result);
        verify(userMapper, never()).mapToUserDto(any(User.class), any(UserDto.class));
    }
}