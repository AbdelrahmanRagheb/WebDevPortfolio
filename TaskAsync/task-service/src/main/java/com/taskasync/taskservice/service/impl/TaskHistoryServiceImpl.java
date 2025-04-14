package com.taskasync.taskservice.service.impl;

import com.taskasync.taskservice.dto.ChangeDetails;
import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.entity.Task;
import com.taskasync.taskservice.entity.TaskHistory;
import com.taskasync.taskservice.exception.ResourceNotFoundException;
import com.taskasync.taskservice.mapper.TaskHistoryMapper;
import com.taskasync.taskservice.mapper.TaskMapper;
import com.taskasync.taskservice.repository.TaskHistoryRepository;
import com.taskasync.taskservice.service.ITaskHistoryService;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class TaskHistoryServiceImpl implements ITaskHistoryService {
    private TaskHistoryRepository taskHistoryRepository;

    @Override
    public TaskHistoryDto trackTaskChange(Task existingTask, TaskDto taskDto) {
        TaskHistory taskHistory = createTaskHistory(existingTask, taskDto);
        if (taskHistory != null) {
            try {

                TaskHistory th = taskHistoryRepository.save(taskHistory);
                return TaskHistoryMapper.mapToTaskHistoryDto(th, new TaskHistoryDto());

            } catch (DataIntegrityViolationException e) {
                throw new DataIntegrityViolationException("Failed to save task history due to data integrity violation", e);
            }
        }
        return null;
    }

    @Override
    public List<TaskHistoryDto> fetchAllChangesToTask(Long taskId) {
        List<TaskHistoryDto> taskHistories = taskHistoryRepository.findByTaskId(taskId);
        if (taskHistories.isEmpty()) {
            throw new ResourceNotFoundException("task history changes", "id", taskId.toString());
        }
        return taskHistories;
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        taskHistoryRepository.deleteByTaskId(taskId);
    }

    private TaskHistory createTaskHistory(Task existingTask, TaskDto taskDto) {
        List<ChangeDetails.Change> changes = new ArrayList<>();
        try {
            Map<String, Object> existingProps = PropertyUtils.describe(existingTask);
            Map<String, Object> dtoProps = PropertyUtils.describe(taskDto);

            // Use reflection to determine which fields are explicitly set in taskDto
            Map<String, Boolean> dtoSetFields = new HashMap<>();
            for (java.lang.reflect.Field field : TaskDto.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(taskDto);
                dtoSetFields.put(field.getName(), value != null);
            }

            for (String propName : existingProps.keySet()) {
                if (propName.equals("id") || propName.equals("comments") || propName.equals("class") ||
                        propName.equals("createdAt") || propName.equals("updatedAt")) { // Exclude audit fields
                    continue;
                }
                if (dtoProps.containsKey(propName) && dtoSetFields.getOrDefault(propName, false)) {
                    Object oldValue = existingProps.get(propName);
                    Object newValue = dtoProps.get(propName);
                    if (!Objects.equals(oldValue, newValue)) {
                        changes.add(new ChangeDetails.Change(propName,
                                oldValue != null ? oldValue.toString() : null,
                                newValue != null ? newValue.toString() : null));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to compare task properties", e);
        }

        if (!changes.isEmpty()) {
            TaskHistory history = new TaskHistory();
            history.setTask(existingTask);
            history.setChangedByUserId(1L); // Adjust as needed
            history.setChangeDetails(new ChangeDetails(changes));
            return history;
        }
        return null;
    }
}