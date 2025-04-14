package com.taskasync.taskservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCommentDto {
    private Long id;

    @NotNull(message = "Commenter ID cannot be null")
    @Min(value = 1, message = "Commenter ID must be a positive number")
    private Long commenterId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;


    private Long taskId;

    private LocalDateTime createdAt;

    private TaskCommentDto parentComment;
}