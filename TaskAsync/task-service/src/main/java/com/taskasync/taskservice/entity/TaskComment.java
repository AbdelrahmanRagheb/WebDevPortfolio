package com.taskasync.taskservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_comments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskComment extends BaseEntity {
    @Column(name = "commenter_id", nullable = false)
    @NotNull(message = "Commenter ID cannot be null")
    @Min(value = 1, message = "Commenter ID must be a positive number")
    private Long commenterId;

    @Column(nullable = false)
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull(message = "Task cannot be null")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private TaskComment parentComment;
}
