package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.Status;
import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskComment;
import com.taskasync.taskservice.exception.BusinessRuleViolationException;
import com.taskasync.taskservice.exception.ResourceNotFoundException;
import com.taskasync.taskservice.mapper.TaskCommentMapper;
import com.taskasync.taskservice.repository.TaskCommentRepository;
import com.taskasync.taskservice.repository.TaskRepository;
import com.taskasync.taskservice.service.ITaskCommentService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskCommentServiceImpl implements ITaskCommentService {
    private NotificationProducerService notificationProducerService;
    private TaskRepository taskRepository;
    private TaskCommentRepository taskCommentRepository;

    @Override
    public void addNewComment(TaskCommentDto taskCommentDto) {
        TaskComment taskComment = new TaskComment();
        Task task = taskRepository.findById(taskCommentDto.getTaskId()).orElseThrow(() -> new ResourceNotFoundException("task", "id", taskCommentDto.getTaskId().toString()));
        taskComment.setTask(task);
        try {
            taskCommentRepository.save(TaskCommentMapper.mapToTaskComment(taskCommentDto, taskComment));
            notificationProducerService.sendTaskCommentNotification(
                    task.getId(),
                    task.getTitle(),
                    task.getCreatorId(),
                    task.getAssignedUsers(),
                    taskCommentDto.getCommenterId(),
                    taskCommentDto.getContent()
            );
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to add comment due to data integrity violation", e);
        }
    }

    @Override
    public void updateComment(TaskCommentDto taskCommentDto) {
        TaskComment taskComment = taskCommentRepository.findById(taskCommentDto.getId()).orElseThrow(() -> new ResourceNotFoundException("comment", "id", taskCommentDto.getId().toString()));
        if (taskComment.getTask().getStatus() == Status.DONE) {
            throw new BusinessRuleViolationException("Cannot update comment for a completed task");
        }
        try {
            taskCommentRepository.save(TaskCommentMapper.mapToTaskComment(taskCommentDto, taskComment));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update comment due to data integrity violation", e);
        }
    }

    @Override
    public void deleteComment(Long id) {
        TaskComment taskComment = taskCommentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("task", "id", id.toString()));
        try {
            taskCommentRepository.delete(taskComment);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to delete comment due to data integrity violation", e);
        }
    }

    @Override
    public TaskCommentDto fetchComment(Long id) {
        TaskComment taskComment = taskCommentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("comment", "id", id.toString()));
        return TaskCommentMapper.mapToTaskCommentDto(taskComment, new TaskCommentDto());
    }
}