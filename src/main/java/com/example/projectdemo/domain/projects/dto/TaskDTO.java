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
    private String projectName;  // JOIN 결과 (실제 DB 컬럼에는 없음)
    private String title;
    private String description;
    private String status;
    private Integer progress;
    private String priority;
    private String reporterEmpNum;
    private String reporterName;  // JOIN 결과 (실제 DB 컬럼에는 없음)
    private String assigneeEmpNum;
    private String assigneeName;  // JOIN 결과 (실제 DB 컬럼에는 없음)
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer estimatedHours;
    private Integer actualHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // 하위 업무 목록 (실제 DB 컬럼에는 없음)
    private List<SubTaskDTO> subTasks;

    // 화면 표시용 메타데이터 (실제 DB 컬럼에는 없음)
    private Integer remainingDays;
    private boolean isOverdue;
}