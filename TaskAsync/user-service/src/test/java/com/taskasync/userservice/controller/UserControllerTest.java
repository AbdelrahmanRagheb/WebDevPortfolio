package com.taskasync.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskasync.userservice.dto.ResponseDto;
import com.taskasync.userservice.dto.UserDto;
import com.taskasync.userservice.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        })
@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public IUserService iUserService() {
            return Mockito.mock(IUserService.class);
        }

        @Bean
        public UserController userController(IUserService iUserService) {
            return new UserController(iUserService);
        }
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(iUserService);

        userDto = new UserDto();
        userDto.setKeycloakSubjectId("keycloak-123");
        userDto.setEmail("test@example.com");
        userDto.setUsername("testuser");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setFullName("Test User");
    }

    @Test
    void createNewUser_Success_ReturnsCreated() throws Exception {
        // Arrange
        when(iUserService.createNewUser(any(UserDto.class))).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1L));

        // Verify service interaction
        verify(iUserService, times(1)).createNewUser(argThat(dto ->
                dto.getKeycloakSubjectId().equals("keycloak-123") &&
                dto.getEmail().equals("test@example.com") &&
                dto.getUsername().equals("testuser")
        ));
    }

    @Test
    void createNewUser_InvalidDto_ReturnsBadRequest() throws Exception {
        // Arrange
        UserDto invalidDto = new UserDto();
        // Missing keycloakSubjectId, email, username (assumed @NotNull or @NotBlank in UserDto)

        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        // Verify service is not called
        verify(iUserService, never()).createNewUser(any());
    }

    @Test
    void getUserIdByKeycloakId_Success_ReturnsOk() throws Exception {
        // Arrange
        String keycloakId = "keycloak-123";
        when(iUserService.getUserIdByKeycloakId(eq(keycloakId))).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(get("/api/users/keycloak/{keycloakId}/id", keycloakId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1L));

        verify(iUserService, times(1)).getUserIdByKeycloakId(keycloakId);
    }

    @Test
    void getUserIdByKeycloakId_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String keycloakId = "keycloak-999";
        when(iUserService.getUserIdByKeycloakId(eq(keycloakId))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/users/keycloak/{keycloakId}/id", keycloakId))
                .andExpect(status().isNotFound());

        verify(iUserService, times(1)).getUserIdByKeycloakId(keycloakId);
    }

    @Test
    void getUserIdByKeycloakId_EmptyKeycloakId_ReturnsBadRequest() throws Exception {
        // Arrange
        String keycloakId = ""; // Assuming validation on path variable

        // Act & Assert
        mockMvc.perform(get("/api/users/keycloak/{keycloakId}/id", " "))
                .andExpect(status().isBadRequest());

        verify(iUserService, never()).getUserIdByKeycloakId(anyString());
    }

    @Test
    void checkUserExists_UserExists_ReturnsOk() throws Exception {
        // Arrange
        String keycloakId = "keycloak-123";
        when(iUserService.checkUserExists(eq(keycloakId))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/{keycloakId}", keycloakId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("user id exists"));

        verify(iUserService, times(1)).checkUserExists(keycloakId);
    }

    @Test
    void checkUserExists_UserDoesNotExist_ReturnsOk() throws Exception {
        // Arrange
        String keycloakId = "keycloak-999";
        when(iUserService.checkUserExists(eq(keycloakId))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/{keycloakId}", keycloakId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value("404"))
                .andExpect(jsonPath("$.statusMsg").value("user with this id does not exist"));

        verify(iUserService, times(1)).checkUserExists(keycloakId);
    }

    @Test
    void checkUserExists_EmptyKeycloakId_ReturnsBadRequest() throws Exception {
        String keycloakId = " ";
        mockMvc.perform(get("/api/users/exists/{keycloakId}",keycloakId))
                .andExpect(status().isBadRequest());
        verify(iUserService, never()).checkUserExists(anyString());
    }

    @Test
    void fetchUserDetails_Success_ReturnsOk() throws Exception {
        // Arrange
        String keycloakId = "keycloak-123";
        when(iUserService.getUserByKeycloakId(eq(keycloakId))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(get("/api/users/fetch/{keycloakId}", keycloakId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.keycloakSubjectId").value("keycloak-123"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(iUserService, times(1)).getUserByKeycloakId(keycloakId);
    }

    @Test
    void fetchUserDetails_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String keycloakId = "keycloak-999";
        when(iUserService.getUserByKeycloakId(eq(keycloakId))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/users/fetch/{keycloakId}", keycloakId))
                .andExpect(status().isNotFound());

        verify(iUserService, times(1)).getUserByKeycloakId(keycloakId);
    }

    @Test
    void fetchUserDetails_EmptyKeycloakId_ReturnsBadRequest() throws Exception {
        // Arrange
        String keycloakId = " ";
        mockMvc.perform(get("/api/users/fetch/{keycloakId}", keycloakId))
                .andExpect(status().isBadRequest());
        verify(iUserService, never()).getUserByKeycloakId(anyString());
    }
}