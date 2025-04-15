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
    private String name;
    private String description;
    private String managerEmpNum;    // Changed to String to match schema
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;
    private String status;
    private Integer depId;
    private Integer progress;        // Added to match mapper
    private Boolean isPublic;        // Added to match mapper
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}