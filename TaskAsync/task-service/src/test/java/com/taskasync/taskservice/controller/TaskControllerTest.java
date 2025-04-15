package com.taskasync.taskservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskasync.taskservice.dto.*;
import com.taskasync.taskservice.service.ITaskHistoryService;
import com.taskasync.taskservice.service.ITaskService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TaskController.class, 
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class,

        })

@Import(TaskControllerTest.TestConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ITaskService iTaskService;

    @Autowired
    private ITaskHistoryService iTaskHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDto taskDto;
    private TaskCommentDto taskCommentDto;
    private TaskHistoryDto taskHistoryDto;


    
    @TestConfiguration
    static class TestConfig {



        @Bean
        public ITaskService iTaskService() {

            return Mockito.mock(ITaskService.class);
        }


        @Bean
        public ITaskHistoryService iTaskHistoryService() {

            return Mockito.mock(ITaskHistoryService.class);
        }

        @Bean
        public TaskController taskController(ITaskService taskService, ITaskHistoryService taskHistoryService) {
            return new TaskController(taskService, taskHistoryService);
        }
    }

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setStatus(Status.TODO);
        taskDto.setCreatorId(3L);
        
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(3L, "abdelrahman");
        assignedUsers.put(21L, "developer");
        taskDto.setAssignedUsers(assignedUsers);

        taskCommentDto = new TaskCommentDto();
        taskCommentDto.setId(1L);
        taskCommentDto.setTaskId(1L);
        taskCommentDto.setContent("Test Comment");

        taskHistoryDto = new TaskHistoryDto();
        taskHistoryDto.setId(1L);
        taskHistoryDto.setTaskId(1L);
        taskHistoryDto.setChangedByUserId(3L);
    }

    @Test
    void createTask_Success_ReturnsCreated() throws Exception {
        
        doNothing().when(iTaskService).createNewTask(any(TaskDto.class));

        
        mockMvc.perform(post("/api/tasks/create")
                        .header("X-User-DatabaseId", "3")
                        .header("X-User-Username", "abdelrahman")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Task created successfully"));

        verify(iTaskService, times(1)).createNewTask(any(TaskDto.class));
    }

    @Test
    void createTask_MissingHeaders_ReturnsBadRequest() throws Exception {
        
        mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());

        verify(iTaskService, never()).createNewTask(any());
    }

    @Test
    void createTask_InvalidTaskDto_ReturnsBadRequest() throws Exception {
        
        TaskDto invalidTaskDto = new TaskDto(); 

        
        mockMvc.perform(post("/api/tasks/create")
                        .header("X-User-DatabaseId", "3")
                        .header("X-User-Username", "abdelrahman")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTaskDto)))
                .andExpect(status().isBadRequest());

        verify(iTaskService, never()).createNewTask(any());
    }

    @Test
    void fetchTask_Success_ReturnsTaskDto() throws Exception {
        
        when(iTaskService.fetchTask(1L)).thenReturn(taskDto);

        
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(iTaskService, times(1)).fetchTask(1L);
    }

    @Test
    void fetchTask_InvalidId_ReturnsBadRequest() throws Exception {
        
        mockMvc.perform(get("/api/tasks/0"))
                .andExpect(status().isBadRequest());

        verify(iTaskService, never()).fetchTask(anyLong());
    }

    @Test
    void fetchTaskComments_Success_ReturnsCommentList() throws Exception {
        
        when(iTaskService.fetchTaskComments(1L)).thenReturn(List.of(taskCommentDto));

        
        mockMvc.perform(get("/api/tasks/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test Comment"));

        verify(iTaskService, times(1)).fetchTaskComments(1L);
    }

    @Test
    void fetchTaskHistory_Success_ReturnsHistoryList() throws Exception {
        
        when(iTaskHistoryService.fetchAllChangesToTask(1L)).thenReturn(List.of(taskHistoryDto));

        
        mockMvc.perform(get("/api/tasks/1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(1));

        verify(iTaskHistoryService, times(1)).fetchAllChangesToTask(1L);
    }

    @Test
    void addDependency_Success_ReturnsCreated() throws Exception {
        
        doNothing().when(iTaskService).addTaskDependency(1L, 2L);

        
        mockMvc.perform(post("/api/tasks/1/dependencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Dependency added"));

        verify(iTaskService, times(1)).addTaskDependency(1L, 2L);
    }

    @Test
    void addDependency_NullDependsOnTaskId_ReturnsBadRequest() throws Exception {
        
        mockMvc.perform(post("/api/tasks/1/dependencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(iTaskService, never()).addTaskDependency(anyLong(), anyLong());
    }

    @Test
    void fetchTaskDependencies_Success_ReturnsTaskList() throws Exception {
        
        when(iTaskService.fetchTaskDependencies(1L)).thenReturn(List.of(taskDto));

        
        mockMvc.perform(get("/api/tasks/1/dependencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(iTaskService, times(1)).fetchTaskDependencies(1L);
    }

    @Test
    void fetchDependentTasks_Success_ReturnsTaskList() throws Exception {
        
        when(iTaskService.fetchDependentTasks(1L)).thenReturn(List.of(taskDto));

        
        mockMvc.perform(get("/api/tasks/1/dependent-tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));

        verify(iTaskService, times(1)).fetchDependentTasks(1L);
    }

    @Test
    void removeDependency_Success_ReturnsOk() throws Exception {
        
        doNothing().when(iTaskService).removeTaskDependency(1L, 2L);

        
        mockMvc.perform(delete("/api/tasks/1/dependencies/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Dependency removed successfully"));

        verify(iTaskService, times(1)).removeTaskDependency(1L, 2L);
    }

    @Test
    void updateTask_Success_ReturnsOk() throws Exception {
        
        doNothing().when(iTaskService).updateTask(any(TaskDto.class));

        
        mockMvc.perform(put("/api/tasks/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Task updated successfully"));

        verify(iTaskService, times(1)).updateTask(any(TaskDto.class));
    }

    @Test
    void deleteTask_Success_ReturnsOk() throws Exception {
        
        doNothing().when(iTaskService).deleteTask(1L);

        
        mockMvc.perform(delete("/api/tasks/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Task deleted successfully"));

        verify(iTaskService, times(1)).deleteTask(1L);
    }
}