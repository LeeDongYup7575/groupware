package com.example.projectdemo.domain.videochat.controller;

import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.videochat.dto.VideoRoomDTO;
import com.example.projectdemo.domain.videochat.dto.VideoRoomParticipantDTO;
import com.example.projectdemo.domain.videochat.service.VideoRoomService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class VideoChatController {

    @Autowired
    private VideoRoomService videoRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmployeesMapper employeesMapper;


    // 화상회의 로비 페이지
    @GetMapping("/videochat")
    public String videoLobby(Model model) {
        List<VideoRoomDTO> rooms = videoRoomService.getAllActiveRooms();
        model.addAttribute("rooms", rooms);
        return "videochat/lobby";
    }

    @PostMapping("/api/videochat/rooms")
    @ResponseBody
    public ResponseEntity<?> createRoom(@RequestBody VideoRoomDTO roomDTO, HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        roomDTO.setCreatedBy(empNum);
        VideoRoomDTO createdRoom = videoRoomService.createRoom(roomDTO);

        return ResponseEntity.ok(createdRoom);
    }

    // 화상회의실 검색
    @GetMapping("/api/videochat/rooms/search")
    @ResponseBody
    public ResponseEntity<?> searchRooms(@RequestParam(required = false) String name) {
        List<VideoRoomDTO> rooms;

        if (name != null && !name.trim().isEmpty()) {
            rooms = videoRoomService.searchRoomsByName(name);
        } else {
            rooms = videoRoomService.getAllActiveRooms();
        }

        return ResponseEntity.ok(rooms);
    }

    // 화상회의실 접속 페이지
    @GetMapping("/videochat/room/{roomId}")
    public String videoRoom(@PathVariable String roomId,
                            @RequestParam(required = false) String password,
                            Model model, HttpServletRequest request) {
        // 회의실 정보 조회
        VideoRoomDTO room = videoRoomService.getRoomById(roomId);

        // 존재하지 않거나 비활성화된 회의실이면 로비로 리다이렉트
        if (room == null || !room.isActive()) {
            return "redirect:/videochat?error=room_not_found";
        }

        // 비밀번호가 필요한 방인지 확인
        if (room.getIsPasswordProtected() && (password == null || !videoRoomService.checkRoomPassword(roomId, password))) {
            return "redirect:/videochat?error=wrong_password&roomId=" + roomId;
        }

        // 현재 참가자 수 확인
        int currentParticipants = videoRoomService.getCurrentParticipantsCount(roomId);

        // 최대 참가자 수 초과 시 접근 제한
        if (room.getMaxParticipants() != 0 && currentParticipants >= room.getMaxParticipants()) {
            return "redirect:/videochat?error=room_full";
        }

        String empNum = (String) request.getAttribute("empNum");

        // 회의실 참가처리
        videoRoomService.joinRoom(roomId, empNum);

        model.addAttribute("room", room);
        model.addAttribute("empNum", empNum);

        return "videochat/room";
    }

    // 회의실 참가자 목록 조회
    @GetMapping("/api/videochat/rooms/{roomId}/participants")
    @ResponseBody
    public ResponseEntity<?> getRoomParticipants(@PathVariable String roomId) {
        List<VideoRoomParticipantDTO> participants = videoRoomService.getRoomParticipants(roomId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping("/api/videochat/rooms/{roomId}/leave")
    @ResponseBody
    public ResponseEntity<?> leaveRoom(@PathVariable String roomId, HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        videoRoomService.leaveRoom(roomId, empNum);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/videochat/rooms/{roomId}/heartbeat")
    @ResponseBody
    public ResponseEntity<?> updateHeartbeat(@PathVariable String roomId, HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        videoRoomService.updateHeartbeat(roomId, empNum);
        return ResponseEntity.ok().build();
    }

    // WebSocket을 통한 시그널링 메시지 처리
    @MessageMapping("/videochat/signal/{roomId}")
    public void processSignal(@DestinationVariable String roomId, Map<String, Object> signal) {
        // 받은 시그널링 메시지를 모든 참가자에게 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/videochat/signal/" + roomId, signal);
    }

    // 채팅 메시지 처리
    @MessageMapping("/videochat/chat/{roomId}")
    public void processChat(@DestinationVariable String roomId, Map<String, Object> chatMessage) {
        // 받은 채팅 메시지를 모든 참가자에게 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/videochat/chat/" + roomId, chatMessage);
    }

    // 참가자 입장/퇴장 알림
    @MessageMapping("/videochat/participant/{roomId}")
    public void notifyParticipantChange(@DestinationVariable String roomId, Map<String, Object> notification) {
        // 참가자 변경 사항을 모든 참가자에게 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/videochat/participant/" + roomId, notification);

        // 최신 참가자 목록 조회
        List<VideoRoomParticipantDTO> participants = videoRoomService.getRoomParticipants(roomId);

        Map<String, Object> participantsUpdate = new HashMap<>();
        participantsUpdate.put("participants", participants);

        // 참가자 목록 업데이트
        messagingTemplate.convertAndSend("/topic/videochat/participants/" + roomId, participantsUpdate);
    }
}