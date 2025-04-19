package com.example.projectdemo.domain.videochat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoom {
    private String id;
    private String name;
    private String password;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean isActive;
    private Integer maxParticipants;
}