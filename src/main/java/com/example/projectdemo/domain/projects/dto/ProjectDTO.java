package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Integer id;
    private String name;
    private String description;
    private String managerEmpNum;
    private String managerName;   //매핑용
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;
    private String status;
    private Integer depId;
    private String depName;        // 매핑용
    private Integer progress;      // 매핑용
    private Boolean isPublic;      // 매핑용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // db와 매핑은 안돼도 필요해서..
    private int taskCount;
    private int completedTaskCount;
    private List<String> memberNames;

    private List<Map<String, String>> members;
}