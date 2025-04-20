package com.example.projectdemo.domain.notification.service;

import com.example.projectdemo.domain.notification.dto.NotificationDTO;
import com.example.projectdemo.domain.notification.entity.Notification;
import com.example.projectdemo.domain.notification.enums.NotificationType;
import com.example.projectdemo.domain.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 새 알림 생성 및 WebSocket으로 전송
     */
    public void createNotification(String empNum, String content, String link, NotificationType type, Integer sourceId) {
        Notification notification = Notification.builder()
                .empNum(empNum)
                .content(content)
                .link(link)
                .type(type.name())
                .isRead(false)
                .sourceId(sourceId)
                .createdAt(LocalDateTime.now())
                .build();

        // DB에 알림 저장
        notificationMapper.insertNotification(notification);

        // WebSocket을 통해 실시간 알림 전송
        NotificationDTO dto = convertToDTO(notification);

        // 사용자별 큐로 메시지 전송
        messagingTemplate.convertAndSendToUser(
                empNum,
                "/queue/notifications",
                dto.toMap()
        );
    }

    /**
     * 안 읽은 알림 목록 조회
     */
    public List<NotificationDTO> getUnreadNotifications(String empNum) {
        return notificationMapper.getUnreadNotifications(empNum)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 모든 알림 목록 조회 (페이징)
     */
    public List<NotificationDTO> getAllNotifications(String empNum, int page, int size) {
        int offset = page * size;
        return notificationMapper.getAllNotifications(empNum, size, offset)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 알림 읽음 표시
     */
    public void markAsRead(Integer id) {
        notificationMapper.markAsRead(id);
    }

    /**
     * 모든 알림 읽음 표시
     */
    public void markAllAsRead(String empNum) {
        notificationMapper.markAllAsRead(empNum);
    }

    /**
     * 안 읽은 알림 개수 조회
     */
    public int countUnreadNotifications(String empNum) {
        return notificationMapper.countUnreadNotifications(empNum);
    }

    /**
     * Entity를 DTO로 변환
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .empNum(notification.getEmpNum())
                .content(notification.getContent())
                .link(notification.getLink())
                .type(notification.getType())
                .isRead(notification.isRead())
                .sourceId(notification.getSourceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}