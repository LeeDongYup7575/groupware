package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Integer id;
    private String title;
    private String description;
    private Integer projectId;
    private String projectName; // 매핑용
    private String assigneeEmpNum;
    private String assigneeName; // 매핑용
    private String reporterEmpNum;
    private String reporterName; // 매핑용
    private String status;
    private String priority;
    private Integer progress;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private Integer estimatedHours;
    private Integer actualHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 추가 정보
    private List<SubTaskDTO> subTasks;
    private long remainingDays; // 남은 일수
    private boolean isOverdue; // 마감일 초과 여부
}