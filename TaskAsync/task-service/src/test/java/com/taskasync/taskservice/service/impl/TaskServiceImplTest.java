package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.Status;
import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.dto.notification.EventType;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskComment;
import com.taskasync.taskservice.entity.TaskDependency;
import com.taskasync.taskservice.exception.BusinessRuleViolationException;
import com.taskasync.taskservice.exception.ResourceNotFoundException;
import com.taskasync.taskservice.exception.TaskAlreadyExistsException;
import com.taskasync.taskservice.repository.TaskDependenciesRepository;
import com.taskasync.taskservice.repository.TaskRepository;
import com.taskasync.taskservice.service.ITaskHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private NotificationProducerService notificationProducerService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskDependenciesRepository taskDependencyRepository;

    @Mock
    private ITaskHistoryService iTaskHistoryService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskDto taskDto;
    private Task task;
    private Task dependsOnTask;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setCreatorId(1L);
        taskDto.setCreatorUsername("creator");
        taskDto.setEstimatedEffort(5L);
        taskDto.setAssignedUsers(new HashMap<>());

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setCreatorId(1L);
        task.setStatus(Status.TODO);
        task.setDependencies(new ArrayList<>());
        task.setDependentTasks(new ArrayList<>());
        task.setAssignedUsers(new HashMap<>());
        task.setComments(new ArrayList<>());

        dependsOnTask = new Task();
        dependsOnTask.setId(2L);
        dependsOnTask.setDependencies(new ArrayList<>());
        dependsOnTask.setDependentTasks(new ArrayList<>());
        dependsOnTask.setComments(new ArrayList<>());
    }

    @Test
    void testCreateNewTask_Success() {
        when(taskRepository.existsByTitleAndCreatorId(anyString(), anyLong())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.createNewTask(taskDto);

        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notificationProducerService, times(1)).sendTaskCreatedNotification(
                eq(EventType.TASK_CREATED), eq(1L), eq("Test Task"), eq(1L), eq("creator"), anyMap());
    }

    @Test
    void testCreateNewTask_AlreadyExists() {
        when(taskRepository.existsByTitleAndCreatorId(anyString(), anyLong())).thenReturn(true);

        assertThrows(TaskAlreadyExistsException.class, () -> taskService.createNewTask(taskDto));
    }

    @Test
    void testCreateNewTask_DataIntegrityViolation() {
        when(taskRepository.existsByTitleAndCreatorId(anyString(), anyLong())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenThrow(new DataIntegrityViolationException("DB error"));

        assertThrows(DataIntegrityViolationException.class, () -> taskService.createNewTask(taskDto));
    }

    @Test
    void testFetchTask_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        TaskDto result = taskService.fetchTask(1L);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(1L, result.getCreatorId());
    }

    @Test
    void testFetchTask_NotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.fetchTask(1L));
    }

    @Test
    void testUpdateTask_Success() {
        
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(iTaskHistoryService.trackTaskChange(any(Task.class), any(TaskDto.class))).thenReturn(new TaskHistoryDto());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        
        taskService.updateTask(taskDto);

        
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notificationProducerService, times(1)).notifyAssignedUsers(
                eq(EventType.TASK_UPDATED), eq(task), eq(taskDto), any(TaskHistoryDto.class));
    }

    @Test
    void testUpdateTask_CompletedTask() {
        task.setStatus(Status.DONE);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        assertThrows(BusinessRuleViolationException.class, () -> taskService.updateTask(taskDto));
    }

    @Test
    void testUpdateTask_NegativeEffort() {
        taskDto.setEstimatedEffort(-1L);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskDto));
    }

    @Test
    void testUpdateTask_TitleAlreadyExists() {
        taskDto.setTitle("New Title");
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.existsByTitleAndCreatorId(eq("New Title"), anyLong())).thenReturn(true);

        assertThrows(TaskAlreadyExistsException.class, () -> taskService.updateTask(taskDto));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(iTaskHistoryService, times(1)).deleteByTaskId(1L);
        verify(taskRepository, times(1)).delete(task);
        verify(notificationProducerService, times(1)).sendTaskDeletedNotification(
                eq(1L), eq("Test Task"), eq(1L), anyMap());
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L));
    }

    @Test
    void testFetchTaskComments_Success() {
        
        TaskComment comment = new TaskComment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        comment.setTask(task); 
        task.getComments().add(comment);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        
        List<TaskCommentDto> result = taskService.fetchTaskComments(1L);

        
        assertEquals(1, result.size());
        assertEquals("Test Comment", result.get(0).getContent());
    }

    @Test
    void testFetchTaskComments_TaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.fetchTaskComments(1L));
    }

    @Test
    void testAddTaskDependency_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(dependsOnTask));
        when(taskDependencyRepository.save(any(TaskDependency.class))).thenReturn(new TaskDependency());

        taskService.addTaskDependency(1L, 2L);

        verify(taskDependencyRepository, times(1)).save(any(TaskDependency.class));
        verify(taskRepository, times(2)).save(any(Task.class));
    }

    @Test
    void testAddTaskDependency_SelfDependency() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        assertThrows(BusinessRuleViolationException.class, () -> taskService.addTaskDependency(1L, 1L));
    }

    @Test
    void testAddTaskDependency_CircularDependency() {
        TaskDependency dependency = new TaskDependency();
        dependency.setTask(dependsOnTask);
        dependency.setDependsOnTask(task);
        dependsOnTask.getDependencies().add(dependency);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(dependsOnTask));

        assertThrows(BusinessRuleViolationException.class, () -> taskService.addTaskDependency(1L, 2L));
    }

    @Test
    void testFetchTaskDependencies_Success() {
        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        task.getDependencies().add(dependency);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        List<TaskDto> result = taskService.fetchTaskDependencies(1L);

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().getId());
    }

    @Test
    void testFetchDependentTasks_Success() {
        TaskDependency dependency = new TaskDependency();
        dependency.setTask(dependsOnTask);
        dependency.setDependsOnTask(task);
        task.getDependentTasks().add(dependency);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        List<TaskDto> result = taskService.fetchDependentTasks(1L);

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().getId());
    }

    @Test
    void testRemoveTaskDependency_Success() {
        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        task.getDependencies().add(dependency);
        dependsOnTask.getDependentTasks().add(dependency);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(dependsOnTask));

        taskService.removeTaskDependency(1L, 2L);

        verify(taskDependencyRepository, times(1)).delete(dependency);
        verify(taskRepository, times(2)).save(any(Task.class));
    }

    @Test
    void testRemoveTaskDependency_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(dependsOnTask));

        assertThrows(ResourceNotFoundException.class, () -> taskService.removeTaskDependency(1L, 2L));
    }
}