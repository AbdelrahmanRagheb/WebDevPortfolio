package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.ChangeDetails;
import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskHistory;
import com.taskasync.taskservice.exception.ResourceNotFoundException;
import com.taskasync.taskservice.repository.TaskHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskHistoryServiceImplTest {

    @Mock
    private TaskHistoryRepository taskHistoryRepository;

    @InjectMocks
    private TaskHistoryServiceImpl taskHistoryService;

    private Task existingTask;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("New Title");
        taskDto.setDescription("New Description");
    }

    @Test
    void testTrackTaskChange_Success() {
        
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(existingTask);
        taskHistory.setChangedByUserId(1L);
        List<ChangeDetails.Change> changes = new ArrayList<>();
        changes.add(new ChangeDetails.Change("title", "Old Title", "New Title"));
        changes.add(new ChangeDetails.Change("description", "Old Description", "New Description"));
        taskHistory.setChangeDetails(new ChangeDetails(changes));

        TaskHistoryDto taskHistoryDto = new TaskHistoryDto();
        taskHistoryDto.setChangeDetails(new ChangeDetails(changes));

        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(taskHistory);

        
        TaskHistoryDto result = taskHistoryService.trackTaskChange(existingTask, taskDto);

        
        assertNotNull(result);
        assertEquals(2, result.getChangeDetails().getChanges().size());
        verify(taskHistoryRepository, times(1)).save(any(TaskHistory.class));
    }

    @Test
    void testTrackTaskChange_NoChanges() {
        
        taskDto.setTitle("Old Title");
        taskDto.setDescription("Old Description");

        
        TaskHistoryDto result = taskHistoryService.trackTaskChange(existingTask, taskDto);

        
        assertNull(result);
        verify(taskHistoryRepository, never()).save(any(TaskHistory.class));
    }

    @Test
    void testTrackTaskChange_DataIntegrityViolation() {
        
        when(taskHistoryRepository.save(any(TaskHistory.class)))
                .thenThrow(new DataIntegrityViolationException("DB error"));

        
        assertThrows(DataIntegrityViolationException.class, () -> taskHistoryService.trackTaskChange(existingTask, taskDto));
        verify(taskHistoryRepository, times(1)).save(any(TaskHistory.class));
    }

    @Test
    void testFetchAllChangesToTask_Success() {
        
        TaskHistoryDto taskHistoryDto = new TaskHistoryDto();
        List<TaskHistoryDto> taskHistories = Collections.singletonList(taskHistoryDto);
        when(taskHistoryRepository.findByTaskId(anyLong())).thenReturn(taskHistories);

        
        List<TaskHistoryDto> result = taskHistoryService.fetchAllChangesToTask(1L);

        
        assertEquals(1, result.size());
        verify(taskHistoryRepository, times(1)).findByTaskId(1L);
    }

    @Test
    void testFetchAllChangesToTask_NotFound() {
        
        when(taskHistoryRepository.findByTaskId(anyLong())).thenReturn(Collections.emptyList());

        
        assertThrows(ResourceNotFoundException.class, () -> taskHistoryService.fetchAllChangesToTask(1L));
        verify(taskHistoryRepository, times(1)).findByTaskId(1L);
    }

    @Test
    void testDeleteByTaskId() {
        
        taskHistoryService.deleteByTaskId(1L);

        
        verify(taskHistoryRepository, times(1)).deleteByTaskId(1L);
    }
}