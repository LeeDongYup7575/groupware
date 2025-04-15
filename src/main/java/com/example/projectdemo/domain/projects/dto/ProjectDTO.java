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
    private String managerEmpNum;  // Changed from managerId to match schema
    private String managerName;    // Mapping field
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;
    private String status;
    private Integer depId;
    private String depName;        // Mapping field
    private Integer progress;      // Added to match mapper
    private Boolean isPublic;      // Added to match mapper
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for business logic (not directly mapped)
    private int taskCount;         // Total tasks
    private int completedTaskCount; // Completed tasks
    private List<String> memberNames; // Project member names
}