package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {
    private Integer id;
    private String title;
    private String content;
    private String empNum;
    private String priority;
    private LocalDate dueDate;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // 화면 표시용 메타데이터 (실제 DB 컬럼에는 없음)
    private Integer remainingDays;
    private boolean isOverdue;
}