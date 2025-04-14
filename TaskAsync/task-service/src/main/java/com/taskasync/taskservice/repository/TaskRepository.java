package com.taskasync.taskservice.repository;

import com.taskasync.taskservice.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByTitleAndCreatorId(String title, Long creatorId);
}
