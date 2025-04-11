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

        log.info("WebSocket 연결 종료 이벤트: username={}, empNum={}, roomId={}", username, empNum, roomId);

        // 모든 필요한 정보가 있는지 확인
        if (roomId != null && empNum != null) {
            log.info("참가자 퇴장 처리: username={}, empNum={}, roomId={}", username, empNum, roomId);

            try {
                // DB에서 참가자 상태 업데이트
                videoConfService.leaveRoom(roomId, empNum);

                // 다른 참가자들에게 퇴장 알림
                // 사용자 이름이 없는 경우 "알 수 없는 사용자"로 표시
                String displayName = username != null ? username : "알 수 없는 사용자";

                WebRTCMessageDTO message = new WebRTCMessageDTO();
                message.setType("leave");
                message.setFrom(displayName);
                message.setEmpNum(empNum);
                message.setRoomId(roomId);

                messagingTemplate.convertAndSend("/topic/videochat", message);
                log.info("퇴장 메시지 발송 완료: from={}, empNum={}, roomId={}", displayName, empNum, roomId);
            } catch (Exception e) {
                log.error("WebSocket 연결 종료 처리 중 오류 발생: {}", e.getMessage(), e);
            }
        } else {
            log.warn("세션 속성에서 필요한 정보를 찾을 수 없습니다: username={}, empNum={}, roomId={}",
                    username, empNum, roomId);
        }
    }
}