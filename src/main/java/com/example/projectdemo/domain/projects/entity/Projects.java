package com.example.projectdemo.domain.projects.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projects {
    private Integer id;
    private String name;                // 프로젝트명
    private String description;         // 프로젝트 설명
    private Integer managerId;          // 프로젝트 관리자 ID (Employees 테이블 참조)
    private LocalDate startDate;        // 시작일
    private LocalDate endDate;          // 종료 예정일
    private LocalDate actualEndDate;    // 실제 종료일
    private String status;              // 상태(준비중, 진행중, 완료, 보류 등)
    private Integer depId;              // 담당 부서 ID
    private LocalDateTime createdAt;    // 생성일시
    private LocalDateTime updatedAt;    // 수정일시
}