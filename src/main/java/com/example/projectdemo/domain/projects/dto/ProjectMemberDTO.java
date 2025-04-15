package com.example.projectdemo.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {
    private Integer id;
    private Integer projectId;
    private String empNum;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;

    // Mapping fields
    private String name;       // Employee name (used in mapper)
    private String depName;    // Department name
    private String posTitle;   // Position title
}