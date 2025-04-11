package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoList {
    private Integer id;
    private String empNum;         // 직원
    private String title;               // 제목
    private String content;             // 내용
    private Boolean completed;          // 완료 여부
    private LocalDate dueDate;          // 마감일
    private String priority;            // 우선순위
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시
    private LocalDateTime completedAt;  // 완료일시
}