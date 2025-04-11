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
public class TodoDTO {
    private Integer id;
    private String empNum;
    private String title;
    private String content;
    private Boolean completed;
    private LocalDate dueDate;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // 추가 정보
    private long remainingDays; // 남은 일수
    private boolean isOverdue; // 마감일 초과 여부
}