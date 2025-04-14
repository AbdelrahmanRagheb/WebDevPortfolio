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
public class TaskHistoryDto {
    private Long id;

    @NotNull(message = "Task ID cannot be null")
    @Min(value = 1, message = "Task ID must be a positive number")
    private Long taskId;

    @NotNull(message = "Changed-by user ID cannot be null")
    @Min(value = 1, message = "Changed-by user ID must be a positive number")
    private Long changedByUserId;

    @NotNull(message = "Change details cannot be null")
    private ChangeDetails changeDetails;

    private LocalDateTime updatedAt;
}