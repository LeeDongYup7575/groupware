package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tasks {
    private Integer id;
    private String title;               // 업무 제목
    private String description;         // 업무 설명
    private Integer projectId;          // 프로젝트 ID
    private String assigneeEmpNum;      // 담당자 사원번호 (수정)
    private String reporterEmpNum;      // 보고자 사원번호 (수정)
    private String status;              // 상태(미시작, 진행중, 완료, 보류 등)
    private String priority;            // 우선순위 (높음, 중간, 낮음)
    private Integer progress;           // 진행도 (0-100%)
    private LocalDate startDate;        // 시작일
    private LocalDate dueDate;          // 마감일
    private LocalDate completedDate;    // 완료일
    private Integer estimatedHours;     // 예상 소요 시간
    private Integer actualHours;        // 실제 소요 시간
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시
}