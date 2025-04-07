package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.videoconf.dto.WebRTCMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VideoConfWebSocketController {

    @MessageMapping("/videochat.sendMessage")
    @SendTo("/topic/videochat")
    public WebRTCMessageDTO sendMessage(@Payload WebRTCMessageDTO webRTCMessage) {
        return webRTCMessage;
    }

    @MessageMapping("/videochat.joinRoom")
    @SendTo("/topic/videochat")
    public WebRTCMessageDTO joinRoom(@Payload WebRTCMessageDTO webRTCMessage,
                                     SimpMessageHeaderAccessor headerAccessor) {
        // 세션에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("username", webRTCMessage.getFrom());
        headerAccessor.getSessionAttributes().put("empNum", webRTCMessage.getEmpNum());
        headerAccessor.getSessionAttributes().put("roomId", webRTCMessage.getRoomId());

        return webRTCMessage;
    }
}