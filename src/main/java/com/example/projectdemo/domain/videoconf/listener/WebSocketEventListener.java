package com.example.projectdemo.domain.videoconf.listener;

import com.example.projectdemo.domain.videoconf.dto.WebRTCMessageDTO;
import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final VideoConfService videoConfService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String empNum = (String) headerAccessor.getSessionAttributes().get("empNum");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        if (username != null && empNum != null && roomId != null) {
            log.info("User Disconnected: {} from room {}", username, roomId);

            // DB에서 참가자 상태 업데이트
            videoConfService.leaveRoom(roomId, empNum);

            // 다른 참가자들에게 퇴장 알림
            WebRTCMessageDTO message = new WebRTCMessageDTO();
            message.setType("leave");
            message.setFrom(username);
            message.setEmpNum(empNum);
            message.setRoomId(roomId);

            messagingTemplate.convertAndSend("/topic/videochat", message);
        }
    }
}