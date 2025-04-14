package com.taskasync.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDependencyDTO {
    private Long id;
    private Long taskId;
    private Long dependsOnTaskId;
}