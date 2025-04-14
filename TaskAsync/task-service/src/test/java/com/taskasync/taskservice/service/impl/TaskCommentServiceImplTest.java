package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.Status;
import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskComment;
import com.taskasync.taskservice.exception.BusinessRuleViolationException;
import com.taskasync.taskservice.exception.ResourceNotFoundException;
import com.taskasync.taskservice.repository.TaskCommentRepository;
import com.taskasync.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskCommentServiceImplTest {

    @Mock
    private NotificationProducerService notificationProducerService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskCommentRepository taskCommentRepository;

    @InjectMocks
    private TaskCommentServiceImpl taskCommentService;

    private TaskCommentDto taskCommentDto;
    private Task task;
    private TaskComment taskComment;

    @BeforeEach
    void setUp() {
        taskCommentDto = new TaskCommentDto();
        taskCommentDto.setId(1L);
        taskCommentDto.setTaskId(1L);
        taskCommentDto.setCommenterId(1L);
        taskCommentDto.setContent("Test Comment");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setCreatorId(1L);
        task.setStatus(Status.TODO);
        task.setAssignedUsers(new HashMap<>());

        taskComment = new TaskComment();
        taskComment.setId(1L);
        taskComment.setTask(task);
        taskComment.setContent("Test Comment");
    }

    @Test
    void testAddNewComment_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskCommentRepository.save(any(TaskComment.class))).thenReturn(taskComment);

        taskCommentService.addNewComment(taskCommentDto);

        verify(taskCommentRepository, times(1)).save(any(TaskComment.class));
        verify(notificationProducerService, times(1)).sendTaskCommentNotification(
                eq(1L), eq("Test Task"), eq(1L), anyMap(), eq(1L), eq("Test Comment"));
    }

    @Test
    void testAddNewComment_TaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskCommentService.addNewComment(taskCommentDto));
    }

    @Test
    void testAddNewComment_DataIntegrityViolation() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskCommentRepository.save(any(TaskComment.class))).thenThrow(new DataIntegrityViolationException("DB error"));

        assertThrows(DataIntegrityViolationException.class, () -> taskCommentService.addNewComment(taskCommentDto));
    }

    @Test
    void testUpdateComment_Success() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.of(taskComment));
        when(taskCommentRepository.save(any(TaskComment.class))).thenReturn(taskComment);

        taskCommentService.updateComment(taskCommentDto);

        verify(taskCommentRepository, times(1)).save(any(TaskComment.class));
    }

    @Test
    void testUpdateComment_CompletedTask() {
        task.setStatus(Status.DONE);
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.of(taskComment));

        assertThrows(BusinessRuleViolationException.class, () -> taskCommentService.updateComment(taskCommentDto));
    }

    @Test
    void testUpdateComment_NotFound() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskCommentService.updateComment(taskCommentDto));
    }

    @Test
    void testDeleteComment_Success() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.of(taskComment));

        taskCommentService.deleteComment(1L);

        verify(taskCommentRepository, times(1)).delete(taskComment);
    }

    @Test
    void testDeleteComment_NotFound() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskCommentService.deleteComment(1L));
    }

    @Test
    void testDeleteComment_DataIntegrityViolation() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.of(taskComment));
        doThrow(new DataIntegrityViolationException("DB error")).when(taskCommentRepository).delete(taskComment);

        assertThrows(DataIntegrityViolationException.class, () -> taskCommentService.deleteComment(1L));
    }

    @Test
    void testFetchComment_Success() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.of(taskComment));

        TaskCommentDto result = taskCommentService.fetchComment(1L);

        assertNotNull(result);
        assertEquals("Test Comment", result.getContent());
    }

    @Test
    void testFetchComment_NotFound() {
        when(taskCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskCommentService.fetchComment(1L));
    }
}