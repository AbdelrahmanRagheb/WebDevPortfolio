package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.ChangeDetails;
import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.dto.notification.*;
import com.taskasync.taskservice.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationProducerServiceTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private NotificationProducerService notificationProducerService;

    private Task existingTask;
    private TaskDto taskDto;
    private TaskHistoryDto taskHistoryDto;

    @BeforeEach
    void setUp() {
        existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Test Task");
        existingTask.setCreatorId(1L);
        Map<Long, String> oldAssignedUsers = new HashMap<>();
        oldAssignedUsers.put(1L, "creator");
        oldAssignedUsers.put(2L, "developer");
        existingTask.setAssignedUsers(oldAssignedUsers);

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setCreatorId(1L);
        taskDto.setCreatorUsername("creator");
        Map<Long, String> newAssignedUsers = new HashMap<>();
        newAssignedUsers.put(1L, "creator");
        newAssignedUsers.put(3L, "tester");
        taskDto.setAssignedUsers(newAssignedUsers);

        taskHistoryDto = new TaskHistoryDto();
        List<ChangeDetails.Change> changes = new ArrayList<>();
        changes.add(new ChangeDetails.Change("title", "Old Title", "Test Task"));
        taskHistoryDto.setChangeDetails(new ChangeDetails(changes));
    }

    @Test
    void testSendTaskCreatedNotification_Success() {
        
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(2L, "developer");
        when(streamBridge.send(eq("taskNotification-out-0"), any(TaskCreationNotificationDto.class))).thenReturn(true);

        
        notificationProducerService.sendTaskCreatedNotification(EventType.TASK_CREATED, 1L, "Test Task", 1L, "creator", assignedUsers);

        
        verify(streamBridge, times(1)).send(eq("taskNotification-out-0"), any(TaskCreationNotificationDto.class));
    }

    @Test
    void testSendTaskCreatedNotification_EmptyAssignedUsers() {
        
        when(streamBridge.send(eq("taskNotification-out-0"), any(TaskCreationNotificationDto.class))).thenReturn(true);

        
        notificationProducerService.sendTaskCreatedNotification(EventType.TASK_CREATED, 1L, "Test Task", 1L, "creator", null);

        
        verify(streamBridge, times(1)).send(eq("taskNotification-out-0"), any(TaskCreationNotificationDto.class));
    }

    @Test
    void testSendTaskCreatedNotification_Failure() {
        
        when(streamBridge.send(eq("taskNotification-out-0"), any(TaskCreationNotificationDto.class)))
                .thenThrow(new RuntimeException("Failed to send"));

        
        assertThrows(RuntimeException.class, () ->
            notificationProducerService.sendTaskCreatedNotification(EventType.TASK_CREATED, 1L, "Test Task", 1L, "creator", new HashMap<>()));
        verify(streamBridge, times(1)).send(eq("taskNotification-out-0"), any(TaskCreationNotificationDto.class));
    }

    @Test
    void testNotifyAssignedUsers_Success() {
        
        when(streamBridge.send(eq("taskNotification-out-0"), any(TaskUpdatedNotificationDto.class))).thenReturn(true);

        
        notificationProducerService.notifyAssignedUsers(EventType.TASK_UPDATED, existingTask, taskDto, taskHistoryDto);

        
        verify(streamBridge, times(1)).send(eq("taskNotification-out-0"), any(TaskUpdatedNotificationDto.class));
    }

    @Test
    void testNotifyAssignedUsers_NoChanges() {
        
        taskDto.setAssignedUsers(existingTask.getAssignedUsers());
        taskHistoryDto.setChangeDetails(new ChangeDetails(new ArrayList<>()));

        
        notificationProducerService.notifyAssignedUsers(EventType.TASK_UPDATED, existingTask, taskDto, taskHistoryDto);

        
        verify(streamBridge, never()).send(anyString(), any());
    }

    @Test
    void testSendTaskDeletedNotification_Success() {
        
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(2L, "developer");
        when(streamBridge.send(eq("taskNotification-out-0"), any(TaskDeletedNotificationDto.class))).thenReturn(true);

        
        notificationProducerService.sendTaskDeletedNotification(1L, "Test Task", 1L, assignedUsers);

        
        verify(streamBridge, times(1)).send(eq("taskNotification-out-0"), any(TaskDeletedNotificationDto.class));
    }

    @Test
    void testSendTaskCommentNotification_Success() {
        
        Map<Long, String> assignedUsers = new HashMap<>();
        assignedUsers.put(2L, "developer");
        when(streamBridge.send(eq("taskNotification-out-0"), any(TaskCommentNotificationDto.class))).thenReturn(true);

        
        notificationProducerService.sendTaskCommentNotification(1L, "Test Task", 1L, assignedUsers, 2L, "New Comment");

        
        verify(streamBridge, times(1)).send(eq("taskNotification-out-0"), any(TaskCommentNotificationDto.class));
    }
}