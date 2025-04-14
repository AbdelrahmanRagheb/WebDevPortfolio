package com.taskasync.taskservice.controller;

import com.taskasync.taskservice.dto.ResponseDto;
import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.dto.TaskDto;
import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.service.ITaskHistoryService;
import com.taskasync.taskservice.service.ITaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ITaskService iTaskService;
    private final ITaskHistoryService iTaskHistoryService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createTask(@RequestHeader("X-User-DatabaseId") Long databaseId,
            @RequestHeader("X-User-Username") String username
            , @Valid @RequestBody TaskDto taskDto) {
        taskDto.setCreatorId(databaseId);
        taskDto.setCreatorUsername(username);
        iTaskService.createNewTask(taskDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201", "Task created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> fetchTask(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id) {
        TaskDto taskDto = iTaskService.fetchTask(id);
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TaskCommentDto>> fetchTaskComments(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id) {
        List<TaskCommentDto> commentDtos = iTaskService.fetchTaskComments(id);
        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistoryDto>> fetchTaskHistory(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id) {
        List<TaskHistoryDto> taskHistoryDtos = iTaskHistoryService.fetchAllChangesToTask(id);
        return new ResponseEntity<>(taskHistoryDtos, HttpStatus.OK);
    }

    @PostMapping("/{id}/dependencies")
    public ResponseEntity<ResponseDto> addDependency(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id,
            @RequestBody @NotNull(message = "Depends-on Task ID cannot be null") Long dependsOnTaskId) {
        iTaskService.addTaskDependency(id, dependsOnTaskId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201", "Dependency added"));
    }

    @GetMapping("/{id}/dependencies")
    public ResponseEntity<List<TaskDto>> fetchTaskDependencies(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id) {
        List<TaskDto> dependencies = iTaskService.fetchTaskDependencies(id);
        return new ResponseEntity<>(dependencies, HttpStatus.OK);
    }

    @GetMapping("/{id}/dependent-tasks")
    public ResponseEntity<List<TaskDto>> fetchDependentTasks(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id) {
        List<TaskDto> dependentTasks = iTaskService.fetchDependentTasks(id);
        return new ResponseEntity<>(dependentTasks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/dependencies/{dependsOnTaskId}")
    public ResponseEntity<ResponseDto> removeDependency(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id,
            @PathVariable @Min(value = 1, message = "Depends-on Task ID must be a positive number") Long dependsOnTaskId) {
        iTaskService.removeTaskDependency(id, dependsOnTaskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200", "Dependency removed successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto> updateTask(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id,
            @Valid @RequestBody TaskDto taskDto) {
        taskDto.setId(id);
        iTaskService.updateTask(taskDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200", "Task updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto> deleteTask(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long id) {
        iTaskService.deleteTask(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200", "Task deleted successfully"));
    }
}