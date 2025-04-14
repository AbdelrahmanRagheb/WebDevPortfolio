package com.taskasync.taskservice.service;

import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.entity.Task;

import java.util.List;


public interface ITaskHistoryService {
    TaskHistoryDto trackTaskChange(Task existingTask, TaskDto taskDto);
    List<TaskHistoryDto> fetchAllChangesToTask(Long taskId);
    void deleteByTaskId(Long taskId);
}
