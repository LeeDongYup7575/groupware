package com.example.projectdemo.domain.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Integer id;
    private String empNum;
    private String content;
    private String link;
    private String type;
    private boolean isRead;
    private Integer sourceId;
    private LocalDateTime createdAt;
}
