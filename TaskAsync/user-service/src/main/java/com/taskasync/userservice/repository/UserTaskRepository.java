package com.taskasync.userservice.repository;

import com.taskasync.userservice.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    Optional<UserTask> findByUserIdAndTaskId(Long userId, Long taskId);
    void deleteByUserIdAndTaskId(Long userId, Long taskId);
}