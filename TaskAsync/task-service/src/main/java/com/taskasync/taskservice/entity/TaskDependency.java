package com.taskasync.taskservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_dependencies")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDependency extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull(message = "Task ID cannot be null")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "depends_on_task_id", nullable = false)
    @NotNull(message = "Depends-on Task ID cannot be null")
    private Task dependsOnTask;
}