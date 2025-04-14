package com.taskasync.taskservice.mapper;

import com.taskasync.taskservice.entity.TaskDependency;
import com.taskasync.taskservice.dto.TaskDependencyDTO;

public class TaskDependencyMapper {

    public static TaskDependencyDTO mapToTaskDependencyDTO(TaskDependency taskDependency, TaskDependencyDTO taskDependencyDTO) {
        taskDependencyDTO.setId(taskDependency.getId());
        taskDependencyDTO.setTaskId(taskDependency.getTask().getId());
        taskDependencyDTO.setDependsOnTaskId(taskDependency.getDependsOnTask().getId());
        return taskDependencyDTO;
    }

    public static TaskDependency mapToTaskDependency(TaskDependencyDTO taskDependencyDTO, TaskDependency taskDependency) {
        taskDependency.setId(taskDependencyDTO.getId());
        return taskDependency;
    }
}