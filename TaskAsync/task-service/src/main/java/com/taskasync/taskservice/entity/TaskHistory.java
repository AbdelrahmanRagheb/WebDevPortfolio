package com.taskasync.taskservice.entity;

import com.taskasync.taskservice.dto.ChangeDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "task_history")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(insertable = false, updatable = false))
})
public class TaskHistory extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull(message = "Task cannot be null")
    private Task task;

    @Column(name = "changed_by_user_id", nullable = false)
    @NotNull(message = "Changed-by user ID cannot be null")
    @Min(value = 1, message = "Changed-by user ID must be a positive number")
    private Long changedByUserId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "change_details", columnDefinition = "JSON", nullable = false)
    @NotNull(message = "Change details cannot be null")
    private ChangeDetails changeDetails;
}