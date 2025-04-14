package com.taskasync.taskservice.service;

import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.dto.TaskDto;


import java.util.List;


public interface ITaskService {
    void createNewTask(TaskDto taskDto);
    TaskDto fetchTask(Long taskId);
    void updateTask(TaskDto taskDto);
    void deleteTask(Long id);
    List<TaskCommentDto> fetchTaskComments(Long id);
    void addTaskDependency(Long taskId, Long dependsOnTaskId);
    List<TaskDto> fetchTaskDependencies(Long taskId);
    List<TaskDto> fetchDependentTasks(Long taskId);
    void removeTaskDependency(Long taskId, Long dependsOnTaskId);
}
