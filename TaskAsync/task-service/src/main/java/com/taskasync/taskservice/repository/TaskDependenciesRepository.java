package com.taskasync.taskservice.repository;

import com.taskasync.taskservice.entity.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDependenciesRepository extends JpaRepository<TaskDependency, Long> {
}
