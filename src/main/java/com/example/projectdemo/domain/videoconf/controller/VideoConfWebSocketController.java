package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.videoconf.dto.WebRTCMessageDTO;
import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VideoConfWebSocketController {

    private final VideoConfService videoConfService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 일반 WebRTC 메시지 처리
     */
    @MessageMapping("/videochat.sendMessage")
    public void handleWebSocketMessage(@Payload WebRTCMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            log.debug("WebSocket 메시지 수신: type={}, from={}, roomId={}",
                    message.getType(), message.getFrom(), message.getRoomId());

            // 세션에 사용자 정보 저장 (필요한 경우)
            if (headerAccessor.getSessionAttributes() != null && "join".equals(message.getType())) {
                headerAccessor.getSessionAttributes().put("username", message.getFrom());
                headerAccessor.getSessionAttributes().put("empNum", message.getEmpNum());
                headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());
            }

            // 특정 사용자에게만 전달하는 메시지인 경우
            if (message.getTo() != null && !message.getTo().isEmpty()) {
                messagingTemplate.convertAndSendToUser(
                        message.getTo(), "/queue/videochat", message);
            } else {
                // 모든 사용자에게 전달
                messagingTemplate.convertAndSend("/topic/videochat", message);
            }

            // 방 나가기 메시지인 경우 DB 업데이트
            if ("leave".equals(message.getType()) && message.getRoomId() != null && message.getEmpNum() != null) {
                videoConfService.leaveRoom(message.getRoomId(), message.getEmpNum());

                // 모든 클라이언트에게 방 목록 업데이트 알림
                messagingTemplate.convertAndSend("/topic/videochat",
                        new WebRTCMessageDTO(null, null, null, "room-info",
                                videoConfService.findAllActiveRooms(), null, null, null));
            }

            // 하트비트 메시지인 경우 응답
            if ("heartbeat".equals(message.getType()) && message.getRoomId() != null && message.getEmpNum() != null) {
                processHeartbeatMessage(message);
            }
        } catch (Exception e) {
            log.error("WebSocket 메시지 처리 중 오류 발생", e);
        }
    }

    /**
     * 기존 방식과의 호환성을 위한 조인 메시지 처리
     */
    @MessageMapping("/videochat.joinRoom")
    @SendTo("/topic/videochat")
    public WebRTCMessageDTO joinRoom(@Payload WebRTCMessageDTO webRTCMessage,
                                     SimpMessageHeaderAccessor headerAccessor) {
        // 세션에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("username", webRTCMessage.getFrom());
        headerAccessor.getSessionAttributes().put("empNum", webRTCMessage.getEmpNum());
        headerAccessor.getSessionAttributes().put("roomId", webRTCMessage.getRoomId());

        log.info("사용자 입장: username={}, empNum={}, roomId={}",
                webRTCMessage.getFrom(), webRTCMessage.getEmpNum(), webRTCMessage.getRoomId());

        return webRTCMessage;
    }

    /**
     * 하트비트 메시지 처리
     */
    private void processHeartbeatMessage(WebRTCMessageDTO message) {
        try {
            Map<String, Object> result = videoConfService.processHeartbeat(message.getRoomId(), message.getEmpNum());

            WebRTCMessageDTO response = new WebRTCMessageDTO();
            response.setType("heartbeat-response");
            response.setRoomId(message.getRoomId());
            response.setTo(message.getFrom());
            response.setPayload(result);

            if (message.getFrom() != null) {
                messagingTemplate.convertAndSendToUser(
                        message.getFrom(), "/queue/videochat", response);
            } else {
                // 발신자 정보가 없는 경우 일반 메시지로 응답
                messagingTemplate.convertAndSend("/topic/videochat", response);
            }
        } catch (Exception e) {
            log.error("하트비트 메시지 처리 중 오류 발생: roomId={}, empNum={}",
                    message.getRoomId(), message.getEmpNum(), e);
        }
    }
}