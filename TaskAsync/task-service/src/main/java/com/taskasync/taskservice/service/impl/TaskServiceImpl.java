package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.*;
import com.taskasync.taskservice.dto.notification.EventType;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskDependency;
import com.taskasync.taskservice.exception.BusinessRuleViolationException;
import com.taskasync.taskservice.exception.ResourceNotFoundException;
import com.taskasync.taskservice.exception.TaskAlreadyExistsException;
import com.taskasync.taskservice.mapper.TaskCommentMapper;
import com.taskasync.taskservice.mapper.TaskMapper;
import com.taskasync.taskservice.repository.TaskDependenciesRepository;
import com.taskasync.taskservice.repository.TaskHistoryRepository;
import com.taskasync.taskservice.repository.TaskRepository;
import com.taskasync.taskservice.service.ITaskHistoryService;
import com.taskasync.taskservice.service.ITaskService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor

public class TaskServiceImpl implements ITaskService {

    private NotificationProducerService notificationProducerService;
    private TaskRepository taskRepository;
    private TaskDependenciesRepository taskDependencyRepository;
    private ITaskHistoryService iTaskHistoryService;
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);


    @Override
    public void createNewTask(TaskDto taskDto) {
        if (taskRepository.existsByTitleAndCreatorId(taskDto.getTitle(), taskDto.getCreatorId())) {
            throw new TaskAlreadyExistsException("Task with title '" + taskDto.getTitle() + "' already exists for this user");
        }


        try {
            Task newTask = TaskMapper.mapToTask(taskDto, new Task());
            newTask.setCreatorId(taskDto.getCreatorId());
            Task task = taskRepository.save(newTask);

            notificationProducerService.sendTaskCreatedNotification(
                    EventType.TASK_CREATED,
                    task.getId(),
                    task.getTitle(),
                    taskDto.getCreatorId(),
                    taskDto.getCreatorUsername(),
                    taskDto.getAssignedUsers()
            );
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to create task due to data integrity violation", e);
        }
    }

    @Override
    public TaskDto fetchTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("task", "id", taskId.toString())
        );
        return TaskMapper.mapToTaskDto(task, new TaskDto());
    }

    @Override
    public void updateTask(TaskDto taskDto) {
        Task task = taskRepository.findById(taskDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("task", "id", taskDto.getId().toString())
        );
        if (task.getStatus() == Status.DONE) {
            throw new BusinessRuleViolationException("Cannot update a completed task");
        }
        if (taskDto.getEstimatedEffort() != null && taskDto.getEstimatedEffort() < 0) {
            throw new IllegalArgumentException("Estimated effort cannot be negative");
        }
        if (!task.getTitle().equals(taskDto.getTitle()) &&
                taskRepository.existsByTitleAndCreatorId(taskDto.getTitle(), taskDto.getCreatorId())) {
            throw new TaskAlreadyExistsException("Task with title '" + taskDto.getTitle() + "' already exists for this user");
        }
        logger.info("adding row to TaskHistory table");
        TaskHistoryDto taskHistoryDto = iTaskHistoryService.trackTaskChange(task, taskDto);
        logger.info("row added successfully");
        logger.info("casting from TaskDto To Task");
        Task updatedTask = TaskMapper.mapToTask(taskDto, task);

        logger.info("casting happened successfully");
        try {
            logger.info("updating ...............");
            taskRepository.save(updatedTask);
            logger.info("updating happened successfully");

            notificationProducerService.notifyAssignedUsers(EventType.TASK_UPDATED, task, taskDto, taskHistoryDto);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to update task due to data integrity violation", e);
        }
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("task", "id", id.toString()));
        try {

            Long taskId = task.getId();
            String taskTitle = task.getTitle();
            Long creatorId = task.getCreatorId();

            Map<Long, String> assignedUsers = task.getAssignedUsers();
            iTaskHistoryService.deleteByTaskId(id);
            taskRepository.delete(task);

            // Send deletion notification
            notificationProducerService.sendTaskDeletedNotification(
                    taskId,
                    taskTitle,
                    creatorId,
                    assignedUsers
            );

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to delete task due to data integrity violation", e);
        }
    }

    @Override
    public List<TaskCommentDto> fetchTaskComments(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("task", "id", id.toString()));
        List<TaskCommentDto> commentDtos = task.getComments().stream().map(comment ->
                TaskCommentMapper.mapToTaskCommentDto(comment, new TaskCommentDto())
        ).toList();
        return commentDtos;
    }

    @Override
    public void addTaskDependency(Long taskId, Long dependsOnTaskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("task", "id", taskId.toString()));
        Task dependsOnTask = taskRepository.findById(dependsOnTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("task", "id", dependsOnTaskId.toString()));

        if (taskId.equals(dependsOnTaskId)) {
            throw new BusinessRuleViolationException("A task cannot depend on itself");
        }
        if (createsCircularDependency(task, dependsOnTask)) {
            throw new BusinessRuleViolationException("Adding this dependency would create a circular dependency");
        }

        TaskDependency dependency = new TaskDependency();
        dependency.setTask(task);
        dependency.setDependsOnTask(dependsOnTask);
        try {
            taskDependencyRepository.save(dependency);
            task.getDependencies().add(dependency);
            dependsOnTask.getDependentTasks().add(dependency);
            taskRepository.save(task);
            taskRepository.save(dependsOnTask);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Failed to add task dependency due to data integrity violation", e);
        }
    }

    private boolean createsCircularDependency(Task startTask, Task newDependency) {
        return checkCircularDependency(newDependency, startTask.getId(), new java.util.HashSet<>());
    }

    private boolean checkCircularDependency(Task task, Long targetId, java.util.Set<Long> visited) {
        if (visited.contains(task.getId())) {
            return false; // Already visited, no cycle here
        }
        visited.add(task.getId());
        if (task.getId().equals(targetId)) {
            return true; // Found a cycle
        }
        return task.getDependencies().stream()
                .anyMatch(dep -> checkCircularDependency(dep.getDependsOnTask(), targetId, visited));
    }

    @Override
    public List<TaskDto> fetchTaskDependencies(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("task", "id", taskId.toString()));
        return task.getDependencies().stream()
                .map(dep -> TaskMapper.mapToTaskDto(dep.getDependsOnTask(), new TaskDto()))
                .toList();
    }

    @Override
    public List<TaskDto> fetchDependentTasks(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("task", "id", taskId.toString()));
        return task.getDependentTasks().stream()
                .map(dep -> TaskMapper.mapToTaskDto(dep.getTask(), new TaskDto()))
                .toList();
    }

    @Override
    public void removeTaskDependency(Long taskId, Long dependsOnTaskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("task", "id", taskId.toString()));
        Task dependsOnTask = taskRepository.findById(dependsOnTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("task", "id", dependsOnTaskId.toString()));

        TaskDependency dependency = task.getDependencies().stream()
                .filter(dep -> dep.getDependsOnTask().getId().equals(dependsOnTaskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("dependency", "taskId-dependsOnTaskId", taskId + "-" + dependsOnTaskId));

        task.getDependencies().remove(dependency);
        dependsOnTask.getDependentTasks().remove(dependency);
        taskDependencyRepository.delete(dependency);
        taskRepository.save(task);
        taskRepository.save(dependsOnTask);
    }
}