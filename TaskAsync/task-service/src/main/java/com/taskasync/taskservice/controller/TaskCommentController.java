package com.taskasync.taskservice.controller;

import com.taskasync.taskservice.dto.ResponseDto;
import com.taskasync.taskservice.dto.TaskCommentDto;
import com.taskasync.taskservice.service.ITaskCommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tasks/comments")
public class TaskCommentController {
    private static final Logger logger = LoggerFactory.getLogger(TaskCommentController.class);

    private final ITaskCommentService iTaskCommentService;

    @PostMapping("/add/{taskId}")
    public ResponseEntity<ResponseDto> addComment(
            @PathVariable @Min(value = 1, message = "Task ID must be a positive number") Long taskId,
            @Valid @RequestBody TaskCommentDto commentDto) {
        commentDto.setTaskId(taskId);
        logger.info("Received request body: {}", commentDto);
        iTaskCommentService.addNewComment(commentDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201", "Comment added successfully"));
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<TaskCommentDto> fetchTask(
            @PathVariable @Min(value = 1, message = "Comment ID must be a positive number") Long id) {
        TaskCommentDto commentDto = iTaskCommentService.fetchComment(id);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto> updateTask(
            @PathVariable @Min(value = 1, message = "Comment ID must be a positive number") Long id,
            @Valid @RequestBody TaskCommentDto commentDto) {
        commentDto.setId(id);
        iTaskCommentService.updateComment(commentDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200", "Comment updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto> deleteTask(
            @PathVariable @Min(value = 1, message = "Comment ID must be a positive number") Long id) {
        iTaskCommentService.deleteComment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200", "Comment deleted successfully"));
    }
}