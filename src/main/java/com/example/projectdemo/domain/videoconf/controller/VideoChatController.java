package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.videoconf.dto.WebRTCMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VideoChatController {

    // 화상 채팅 페이지 요청 처리
    @GetMapping("/videochat")
    public String getVideoChatPage() {
        return "videochat/videochat";
    }

    // WebRTC 시그널링 메시지 처리
    @MessageMapping("/videochat.sendMessage")
    @SendTo("/topic/videochat")
    public WebRTCMessageDTO sendMessage(@Payload WebRTCMessageDTO webRTCMessage) {
        return webRTCMessage;
    }

    // 사용자 입장 메시지 처리
    @MessageMapping("/videochat.joinRoom")
    @SendTo("/topic/videochat")
    public WebRTCMessageDTO joinRoom(@Payload WebRTCMessageDTO webRTCMessage,
                                  SimpMessageHeaderAccessor headerAccessor) {
        // 세션에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("username", webRTCMessage.getFrom());
        headerAccessor.getSessionAttributes().put("roomId", webRTCMessage.getRoomId());
        return webRTCMessage;
    }
}