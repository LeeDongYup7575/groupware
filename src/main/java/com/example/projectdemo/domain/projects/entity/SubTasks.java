package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubTasks {
    private Integer id;
    private Integer taskId;             // 상위 업무 ID
    private String title;               // 하위 업무 제목
    private String description;         // 하위 업무 설명
    private Integer assigneeId;         // 담당자 ID
    private String status;              // 상태
    private LocalDate dueDate;          // 마감일
    private Integer progress;           // 진행도 (0-100%)
    private LocalDateTime completedAt;  // 완료 시간
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시
}