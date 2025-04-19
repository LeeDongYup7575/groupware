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
    private String managerEmpNum;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;
    private String status;
    private Integer depId;
    private Integer progress;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}