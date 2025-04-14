package com.taskasync.taskservice.entity;

import com.taskasync.taskservice.dto.Category;
import com.taskasync.taskservice.dto.Priority;
import com.taskasync.taskservice.dto.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "task", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title"}, name = "unique_title")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseEntity {
    @Column(name = "creator_id", nullable = false)
    @NotNull(message = "Creator ID cannot be null")
    @Min(value = 1, message = "Creator ID must be a positive number")
    private Long creatorId;

    @Column(nullable = false)
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")

    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Column(name = "due_date")
    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Priority cannot be null")
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status cannot be null")
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "estimated_effort")
    @Min(value = 0, message = "Estimated effort cannot be negative")
    private Long estimatedEffort;

    @Column(name = "recurrence_rule")
    @Min(value = 0, message = "Recurrence rule cannot be negative")
    private Long recurrenceRule;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @NotEmpty(message = "Assigned users cannot be empty if provided")
    private Map<Long, String> assignedUsers;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskDependency> dependencies = new ArrayList<>();

    @OneToMany(mappedBy = "dependsOnTask", cascade = CascadeType.ALL)
    private List<TaskDependency> dependentTasks = new ArrayList<>();
}