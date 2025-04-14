package com.taskasync.taskservice.mapper;

import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.entity.Task;

public class TaskMapper {
    public static TaskDto mapToTaskDto(Task task, TaskDto taskDto) {

        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setCategory(task.getCategory());
        taskDto.setDescription(task.getDescription());
        taskDto.setPriority(task.getPriority());
        taskDto.setStatus(task.getStatus());
        taskDto.setAssignedUsers(task.getAssignedUsers());
        taskDto.setCreatorId(task.getCreatorId());
        taskDto.setDueDate(task.getDueDate());
        taskDto.setEstimatedEffort(task.getEstimatedEffort());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setUpdatedAt(task.getUpdatedAt());
        taskDto.setRecurrenceRule(task.getRecurrenceRule());
        return taskDto;
    }

    public static Task mapToTask(TaskDto taskDto, Task task) {
        task.setTitle(taskDto.getTitle());
        task.setCategory(taskDto.getCategory());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setStatus(taskDto.getStatus());
        task.setAssignedUsers(taskDto.getAssignedUsers());
        task.setDueDate(taskDto.getDueDate());
        task.setEstimatedEffort(taskDto.getEstimatedEffort());
        task.setRecurrenceRule(taskDto.getRecurrenceRule());
        task.setDependencies(taskDto.getDependencies());
        task.setDependentTasks(taskDto.getDependentTasks());
        return task;
    }
}
