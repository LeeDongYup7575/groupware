package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Integer id;
    private Integer projectId;
    private String projectName;  // JOIN 결과
    private String title;
    private String description;
    private String status;
    private Integer progress;
    private String priority;
    private String reporterEmpNum;
    private String reporterName;  // JOIN 결과
    private String assigneeEmpNum;
    private String assigneeName;  // JOIN 결과
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedDate;  // completedAt 대신 completedDate로 변경
    private Integer estimatedHours;
    private Integer actualHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 하위 업무 목록
    private List<SubTaskDTO> subTasks;

    // 화면 표시용 메타데이터
    private Integer remainingDays;
    private boolean isOverdue;
}