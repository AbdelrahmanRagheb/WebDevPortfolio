package com.taskasync.taskservice.repository;

import com.taskasync.taskservice.dto.TaskHistoryDto;
import com.taskasync.taskservice.entity.TaskHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    @Query("SELECT new com.taskasync.taskservice.dto.TaskHistoryDto(th.id, th.task.id,th.changedByUserId, th.changeDetails,th.updatedAt) " +
            "FROM TaskHistory th WHERE th.task.id = ?1")
    List<TaskHistoryDto> findByTaskId(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM TaskHistory th WHERE th.task.id = :taskId")
    void deleteByTaskId(Long taskId);

}
