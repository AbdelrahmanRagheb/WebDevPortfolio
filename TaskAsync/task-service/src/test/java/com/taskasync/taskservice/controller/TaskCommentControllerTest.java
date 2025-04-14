package com.taskasync.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskasync.taskservice.dto.TaskCommentDto;

import com.taskasync.taskservice.service.ITaskCommentService;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Import only necessary static methods
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TaskCommentController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        })
@Import(TaskCommentControllerTest.TestConfig.class)
class TaskCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ITaskCommentService iTaskCommentService; // Autowire the mock

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCommentDto taskCommentDto;
    private TaskCommentDto validCommentDtoForPost;
    private TaskCommentDto validCommentDtoForPut;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ITaskCommentService iTaskCommentService() { // Correct method name
            return Mockito.mock(ITaskCommentService.class);
        }

        @Bean
        public TaskCommentController taskCommentController(ITaskCommentService iTaskCommentService) {
            return new TaskCommentController(iTaskCommentService);
        }
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(iTaskCommentService); // Reset mock

        // Base DTO for fetching
        taskCommentDto = new TaskCommentDto();
        taskCommentDto.setId(1L);
        taskCommentDto.setTaskId(1L); // TaskId usually set by controller or service
        taskCommentDto.setContent("Test Comment");
        taskCommentDto.setCommenterId(100L); // Set valid commenterId

        // Separate DTO for creating (ID is null)
        validCommentDtoForPost = new TaskCommentDto();
        // validCommentDtoForPost.setId(null); // ID should be null for creation
        validCommentDtoForPost.setTaskId(1L); // Will be overwritten by path variable anyway
        validCommentDtoForPost.setContent("Valid New Comment");
        validCommentDtoForPost.setCommenterId(101L); // Needs to be valid

        // Separate DTO for updating (ID might be set by path var)
        validCommentDtoForPut = new TaskCommentDto();
        validCommentDtoForPut.setId(1L); // Set the ID expected for update path
        validCommentDtoForPut.setTaskId(1L); // Usually not changed, but include if needed
        validCommentDtoForPut.setContent("Valid Updated Comment");
        validCommentDtoForPut.setCommenterId(102L); // Needs to be valid
    }

    @Test
    void addComment_Success_ReturnsCreated() throws Exception {
        // Arrange
        // Service method is void, no need to mock unless checking exception
        // doNothing().when(iTaskCommentService).addNewComment(any(TaskCommentDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/tasks/comments/add/1") // Use valid task ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPost))) // Send valid DTO
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Comment added successfully"));

        // Verify service was called, capturing arg to check taskId was set correctly
        verify(iTaskCommentService, times(1)).addNewComment(argThat(dto ->
                dto.getTaskId().equals(1L) && // Check taskId from path was set
                        dto.getContent().equals("Valid New Comment") &&
                        dto.getCommenterId().equals(101L)
        ));
    }

    @Test
    void addComment_InvalidTaskId_ReturnsBadRequest() throws Exception {
        // Arrange - Path variable validation happens before controller method

        // Act & Assert
        mockMvc.perform(post("/api/tasks/comments/add/0") // Invalid taskId path variable (violates @Min(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPost))) // Body is valid, but path isn't
                .andExpect(status().isBadRequest()); // Expect 400 due to path variable validation

        // Service method should never be called
        verify(iTaskCommentService, never()).addNewComment(any());
    }

    @Test
    void addComment_InvalidCommentDto_ReturnsBadRequest() throws Exception {
        // Arrange
        TaskCommentDto invalidCommentDto = new TaskCommentDto();
        // invalidCommentDto.setCommenterId(null); // Violates @NotNull
        invalidCommentDto.setContent(""); // Violates @NotBlank
        invalidCommentDto.setTaskId(1L);

        // Act & Assert
        mockMvc.perform(post("/api/tasks/comments/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest()); // Expect 400 due to @Valid on RequestBody

        verify(iTaskCommentService, never()).addNewComment(any());
    }

    @Test
    void fetchComment_Success_ReturnsCommentDto() throws Exception {
        // Arrange
        when(iTaskCommentService.fetchComment(1L)).thenReturn(taskCommentDto); // taskCommentDto now has commenterId

        // Act & Assert
        mockMvc.perform(get("/api/tasks/comments/fetch/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test Comment"))
                .andExpect(jsonPath("$.commenterId").value(100L)); // Assert the ID set in setUp

        verify(iTaskCommentService, times(1)).fetchComment(1L);
    }

    @Test
    void fetchComment_InvalidId_ReturnsBadRequest() throws Exception {
        // Arrange - Path variable validation happens before controller method

        // Act & Assert
        mockMvc.perform(get("/api/tasks/comments/fetch/0")) // Invalid ID '0' (violates @Min(1))
                .andExpect(status().isBadRequest()); // Expect 400 due to path variable validation

        // Verify service is never called because path validation fails first
        verify(iTaskCommentService, never()).fetchComment(anyLong());
    }

    @Test
    void updateComment_Success_ReturnsOk() throws Exception {
        // Arrange
        long commentId = 1L;
        // Service method is void, no need to mock unless checking exception
        // doNothing().when(iTaskCommentService).updateComment(any(TaskCommentDto.class));

        // Act & Assert
        mockMvc.perform(put("/api/tasks/comments/update/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPut))) // Send valid DTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Comment updated successfully"));

        // Verify service call with specific arguments, checking ID was set from path
        verify(iTaskCommentService, times(1)).updateComment(argThat(dto ->
                dto.getId().equals(commentId) &&
                        dto.getContent().equals("Valid Updated Comment") &&
                        dto.getCommenterId().equals(102L)
        ));
    }

    @Test
    void updateComment_InvalidCommentId_ReturnsBadRequest() throws Exception {
        // Arrange - Path variable validation

        // Act & Assert
        mockMvc.perform(put("/api/tasks/comments/update/0") // Invalid path variable ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPut))) // Body is valid
                .andExpect(status().isBadRequest()); // Expect 400 due to path variable validation

        verify(iTaskCommentService, never()).updateComment(any());
    }

    @Test
    void updateComment_InvalidDto_ReturnsBadRequest() throws Exception {
        // Arrange
        long commentId = 1L;
        TaskCommentDto invalidDto = new TaskCommentDto();
        invalidDto.setId(commentId); // ID from path is okay
        // invalidDto.setCommenterId(null); // Violates @NotNull
        invalidDto.setContent(""); // Violates @NotBlank

        // Act & Assert
        mockMvc.perform(put("/api/tasks/comments/update/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Expect 400 due to @Valid on RequestBody

        verify(iTaskCommentService, never()).updateComment(any());
    }


    @Test
    void deleteComment_Success_ReturnsOk() throws Exception {
        // Arrange
        long commentId = 1L;
        // Service method is void, no need to mock unless checking exception
        // doNothing().when(iTaskCommentService).deleteComment(commentId);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/comments/delete/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Comment deleted successfully"));

        verify(iTaskCommentService, times(1)).deleteComment(commentId);
    }

    @Test
    void deleteComment_InvalidCommentId_ReturnsBadRequest() throws Exception {
        // Arrange - Path variable validation

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/comments/delete/0")) // Invalid path variable ID
                .andExpect(status().isBadRequest()); // Expect 400 due to path variable validation

        verify(iTaskCommentService, never()).deleteComment(anyLong());
    }
}