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
    private ITaskCommentService iTaskCommentService; 

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCommentDto taskCommentDto;
    private TaskCommentDto validCommentDtoForPost;
    private TaskCommentDto validCommentDtoForPut;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ITaskCommentService iTaskCommentService() { 
            return Mockito.mock(ITaskCommentService.class);
        }

        @Bean
        public TaskCommentController taskCommentController(ITaskCommentService iTaskCommentService) {
            return new TaskCommentController(iTaskCommentService);
        }
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(iTaskCommentService); 

        
        taskCommentDto = new TaskCommentDto();
        taskCommentDto.setId(1L);
        taskCommentDto.setTaskId(1L); 
        taskCommentDto.setContent("Test Comment");
        taskCommentDto.setCommenterId(100L); 

        
        validCommentDtoForPost = new TaskCommentDto();
        
        validCommentDtoForPost.setTaskId(1L); 
        validCommentDtoForPost.setContent("Valid New Comment");
        validCommentDtoForPost.setCommenterId(101L); 

        
        validCommentDtoForPut = new TaskCommentDto();
        validCommentDtoForPut.setId(1L); 
        validCommentDtoForPut.setTaskId(1L); 
        validCommentDtoForPut.setContent("Valid Updated Comment");
        validCommentDtoForPut.setCommenterId(102L); 
    }

    @Test
    void addComment_Success_ReturnsCreated() throws Exception {
        
        
        

        
        mockMvc.perform(post("/api/tasks/comments/add/1") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPost))) 
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Comment added successfully"));

        
        verify(iTaskCommentService, times(1)).addNewComment(argThat(dto ->
                dto.getTaskId().equals(1L) && 
                        dto.getContent().equals("Valid New Comment") &&
                        dto.getCommenterId().equals(101L)
        ));
    }

    @Test
    void addComment_InvalidTaskId_ReturnsBadRequest() throws Exception {
        

        
        mockMvc.perform(post("/api/tasks/comments/add/0") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPost))) 
                .andExpect(status().isBadRequest()); 

        
        verify(iTaskCommentService, never()).addNewComment(any());
    }

    @Test
    void addComment_InvalidCommentDto_ReturnsBadRequest() throws Exception {
        
        TaskCommentDto invalidCommentDto = new TaskCommentDto();
        
        invalidCommentDto.setContent(""); 
        invalidCommentDto.setTaskId(1L);

        
        mockMvc.perform(post("/api/tasks/comments/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest()); 

        verify(iTaskCommentService, never()).addNewComment(any());
    }

    @Test
    void fetchComment_Success_ReturnsCommentDto() throws Exception {
        
        when(iTaskCommentService.fetchComment(1L)).thenReturn(taskCommentDto); 

        
        mockMvc.perform(get("/api/tasks/comments/fetch/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test Comment"))
                .andExpect(jsonPath("$.commenterId").value(100L)); 

        verify(iTaskCommentService, times(1)).fetchComment(1L);
    }

    @Test
    void fetchComment_InvalidId_ReturnsBadRequest() throws Exception {
        

        
        mockMvc.perform(get("/api/tasks/comments/fetch/0")) 
                .andExpect(status().isBadRequest()); 

        
        verify(iTaskCommentService, never()).fetchComment(anyLong());
    }

    @Test
    void updateComment_Success_ReturnsOk() throws Exception {
        
        long commentId = 1L;
        
        

        
        mockMvc.perform(put("/api/tasks/comments/update/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPut))) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Comment updated successfully"));

        
        verify(iTaskCommentService, times(1)).updateComment(argThat(dto ->
                dto.getId().equals(commentId) &&
                        dto.getContent().equals("Valid Updated Comment") &&
                        dto.getCommenterId().equals(102L)
        ));
    }

    @Test
    void updateComment_InvalidCommentId_ReturnsBadRequest() throws Exception {
        

        
        mockMvc.perform(put("/api/tasks/comments/update/0") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommentDtoForPut))) 
                .andExpect(status().isBadRequest()); 

        verify(iTaskCommentService, never()).updateComment(any());
    }

    @Test
    void updateComment_InvalidDto_ReturnsBadRequest() throws Exception {
        
        long commentId = 1L;
        TaskCommentDto invalidDto = new TaskCommentDto();
        invalidDto.setId(commentId); 
        
        invalidDto.setContent(""); 

        
        mockMvc.perform(put("/api/tasks/comments/update/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); 

        verify(iTaskCommentService, never()).updateComment(any());
    }


    @Test
    void deleteComment_Success_ReturnsOk() throws Exception {
        
        long commentId = 1L;
        
        

        
        mockMvc.perform(delete("/api/tasks/comments/delete/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Comment deleted successfully"));

        verify(iTaskCommentService, times(1)).deleteComment(commentId);
    }

    @Test
    void deleteComment_InvalidCommentId_ReturnsBadRequest() throws Exception {
        

        
        mockMvc.perform(delete("/api/tasks/comments/delete/0")) 
                .andExpect(status().isBadRequest()); 

        verify(iTaskCommentService, never()).deleteComment(anyLong());
    }
}