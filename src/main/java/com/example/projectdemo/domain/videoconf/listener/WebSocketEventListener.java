package com.example.projectdemo.domain.videoconf.listener;

import com.example.projectdemo.domain.videoconf.dto.VideoRoomParticipantDTO;
import com.example.projectdemo.domain.videoconf.dto.WebRTCMessageDTO;
import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final VideoConfService videoConfService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 세션 속성 가져오기
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String empNum = (String) headerAccessor.getSessionAttributes().get("empNum");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        log.info("WebSocket 연결 종료: username={}, empNum={}, roomId={}", username, empNum, roomId);

        if (roomId != null && empNum != null) {
            try {
                // 참가자 퇴장 처리 (DB 업데이트)
                videoConfService.leaveRoom(roomId, empNum);

                // 모든 클라이언트에게 퇴장 알림
                WebRTCMessageDTO message = WebRTCMessageDTO.builder()
                        .type("leave")
                        .from(username != null ? username : "알 수 없음")
                        .empNum(empNum)
                        .roomId(roomId)
                        .build();

                messagingTemplate.convertAndSend("/topic/videochat", message);

                // 방 정보 업데이트를 모든 클라이언트에게 전송
                List<VideoRoomParticipantDTO> participants = videoConfService.findActiveParticipants(roomId);

                WebRTCMessageDTO participantsUpdate = WebRTCMessageDTO.builder()
                        .type("participants-update")
                        .roomId(roomId)
                        .payload(participants)
                        .build();

                messagingTemplate.convertAndSend("/topic/videochat", participantsUpdate);
            } catch (Exception e) {
                log.error("WebSocket 연결 종료 처리 중 오류: {}", e.getMessage(), e);
            }
        }
    }
}