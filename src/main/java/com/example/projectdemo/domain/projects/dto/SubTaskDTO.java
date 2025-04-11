package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskDTO {
    private Integer id;
    private Integer taskId;
    private String title;
    private String description;
    private String assigneeEmpNum;
    private String assigneeName; // 매핑용
    private String status;
    private LocalDate dueDate;
    private Integer progress;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}