package com.taskasync.taskservice.service;

import com.taskasync.taskservice.dto.TaskCommentDto;


public interface ITaskCommentService {
    void addNewComment(TaskCommentDto taskCommentDto);
    void updateComment(TaskCommentDto taskCommentDto);
    void deleteComment(Long id);
    TaskCommentDto fetchComment(Long id);

}
