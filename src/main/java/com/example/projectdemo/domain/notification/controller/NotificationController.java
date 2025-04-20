package com.example.projectdemo.domain.notification.controller;

import com.example.projectdemo.domain.notification.dto.NotificationDTO;
import com.example.projectdemo.domain.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 안 읽은 알림 목록 조회
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(empNum);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 모든 알림 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        List<NotificationDTO> notifications = notificationService.getAllNotifications(empNum, page, size);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 특정 알림 읽음 표시
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알림 읽음 표시
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        notificationService.markAllAsRead(empNum);
        return ResponseEntity.ok().build();
    }

    /**
     * 안 읽은 알림 개수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> countUnreadNotifications(HttpServletRequest request) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        int count = notificationService.countUnreadNotifications(empNum);
        return ResponseEntity.ok(count);
    }
}