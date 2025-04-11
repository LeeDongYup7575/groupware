package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLogDTO {
    private Integer id;
    private Integer taskId;
    private String empNum;
    private String empName; // 매핑용
    private String logType;
    private String oldValue;
    private String newValue;
    private String comment;
    private LocalDateTime createdAt;
}