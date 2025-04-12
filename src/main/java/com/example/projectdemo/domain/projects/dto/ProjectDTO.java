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
public class ProjectDTO {
    private Integer id;
    private String name;
    private String description;
    private String managerEmpNum;
    private String managerName; // 매핑용
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;
    private String status;
    private Integer depId;
    private String depName; // 매핑용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 추가 정보
    private int taskCount; // 총 업무 수
    private int completedTaskCount; // 완료된 업무 수
    private int progress; // 진행률 (%)
    private List<String> memberNames; // 프로젝트 멤버 이름 목록
}