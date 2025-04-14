package com.taskasync.userservice.entity;


import com.taskasync.userservice.dto.notification.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "user_notifications")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserNotification  extends BaseEntity {


    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private EventType notificationType;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Message cannot be blank")
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> metadata;
}
