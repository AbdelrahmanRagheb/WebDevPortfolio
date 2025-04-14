package com.taskasync.taskservice.dto;

import com.taskasync.taskservice.entity.TaskDependency;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;

    @Transient
    private Long creatorId;

    private String creatorUsername;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDateTime dueDate;

    @NotNull(message = "Priority cannot be null")
    private Priority priority = Priority.MEDIUM;

    @NotNull(message = "Status cannot be null")
    private Status status = Status.TODO;

    private Category category;

    @Min(value = 0, message = "Estimated effort cannot be negative")
    private Long estimatedEffort;

    @Min(value = 0, message = "Recurrence rule cannot be negative")
    private Long recurrenceRule;


    @NotEmpty(message = "Assigned users cannot be empty if provided")
    private Map<Long, String> assignedUsers;


    private List<TaskDependency> dependencies ;

    private List<TaskDependency> dependentTasks;

}