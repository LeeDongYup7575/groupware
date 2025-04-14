package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskDTO {
    private Integer id;
    private Integer taskId;
    private String title;
    private String description;
    private boolean completed;
    private String assigneeEmpNum;
    private String assigneeName;  // JOIN 결과 (실제 DB 컬럼에는 없음)
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}