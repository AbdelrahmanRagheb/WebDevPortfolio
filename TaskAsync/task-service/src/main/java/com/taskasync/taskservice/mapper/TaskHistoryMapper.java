package com.taskasync.taskservice.mapper;

import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.entity.TaskHistory;

public class TaskHistoryMapper {

    public static TaskHistoryDto mapToTaskHistoryDto(TaskHistory taskHistory, TaskHistoryDto taskHistoryDto) {

        taskHistoryDto.setId(taskHistory.getId());
        taskHistoryDto.setTaskId(taskHistory.getTask().getId());
        taskHistoryDto.setChangeDetails(taskHistory.getChangeDetails());
        taskHistoryDto.setUpdatedAt(taskHistory.getUpdatedAt());
        taskHistoryDto.setChangedByUserId(taskHistory.getChangedByUserId());
        return taskHistoryDto;
    }
    public static TaskHistory mapToTaskHistory(TaskHistoryDto taskHistoryDto,TaskHistory taskHistory) {
        taskHistory.setId(taskHistoryDto.getId());
        taskHistory.setChangeDetails(taskHistoryDto.getChangeDetails());
        taskHistory.setUpdatedAt(taskHistoryDto.getUpdatedAt());
        taskHistory.setChangedByUserId(taskHistoryDto.getChangedByUserId());
        return taskHistory;
    }


}
