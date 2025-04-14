package com.taskasync.taskservice.mapper;

import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.entity.TaskComment;

public class TaskCommentMapper {
    public static TaskCommentDto mapToTaskCommentDto(TaskComment taskComment, TaskCommentDto taskCommentDto) {
        if (taskComment == null) {
            return taskCommentDto;
        }
        taskCommentDto.setCommenterId(taskComment.getCommenterId());
        taskCommentDto.setId(taskComment.getId());
        taskCommentDto.setContent(taskComment.getContent());
        taskCommentDto.setCreatedAt(taskComment.getCreatedAt());
        taskCommentDto.setTaskId(taskComment.getTask().getId());
        if (taskComment.getParentComment() != null) {
            taskCommentDto.setParentComment(mapToTaskCommentDto(taskComment.getParentComment(), new TaskCommentDto()));
        } else {
            taskCommentDto.setParentComment(null);
        }
        return taskCommentDto;
    }

    public static TaskComment mapToTaskComment(TaskCommentDto taskCommentDto, TaskComment taskComment) {

        taskComment.setCommenterId(taskCommentDto.getCommenterId());
        taskComment.setContent(taskCommentDto.getContent());
        if (taskCommentDto.getParentComment() != null) {

        taskComment.setParentComment(mapToTaskComment(taskCommentDto.getParentComment(),new TaskComment()));
        }
        taskComment.setCreatedAt(taskCommentDto.getCreatedAt());
        taskComment.setId(taskCommentDto.getId());
        return taskComment;
    }
}
