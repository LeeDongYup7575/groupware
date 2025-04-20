package com.example.projectdemo.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer id;
    private String empNum;
    private String content;
    private String link;
    private String type;
    private boolean isRead;
    private Integer sourceId;
    private LocalDateTime createdAt;

    // JSON 변환을 위한 메소드
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("content", content);
        map.put("link", link);
        map.put("type", type);
        map.put("isRead", isRead);
        map.put("createdAt", createdAt.toString());
        return map;
    }
}