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
    private String status;         // completed 대신 status 필드로 변경
    private Integer progress;      // 추가: progress 필드
    private String assigneeEmpNum;
    private String assigneeName;   // JOIN 결과
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}