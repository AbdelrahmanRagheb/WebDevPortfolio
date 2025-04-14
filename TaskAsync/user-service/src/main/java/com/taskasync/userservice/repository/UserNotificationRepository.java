package com.taskasync.userservice.repository;

import com.taskasync.userservice.dto.notification.EventType;
import com.taskasync.userservice.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserId(Long userId);

    @Query("SELECT un FROM UserNotification un WHERE un.userId = :userId AND JSON_EXTRACT(un.metadata, '$.task_id') = :taskId")
    List<UserNotification> findByUserIdAndTaskId(@Param("userId") Long userId, @Param("taskId") Long taskId);

    @Query("SELECT un FROM UserNotification un WHERE un.userId = :userId AND un.notificationType = :type")
    List<UserNotification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") EventType type);
}
