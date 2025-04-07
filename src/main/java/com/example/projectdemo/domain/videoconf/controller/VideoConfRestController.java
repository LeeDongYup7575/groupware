package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.videoconf.dto.VideoRoomCreateDTO;
import com.example.projectdemo.domain.videoconf.dto.VideoRoomDTO;
import com.example.projectdemo.domain.videoconf.dto.VideoRoomParticipantDTO;
import com.example.projectdemo.domain.videoconf.dto.VideoRoomJoinDTO;
import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videoconf")
@RequiredArgsConstructor
public class VideoConfRestController {

    private final VideoConfService videoConfService;

    @GetMapping("/rooms")
    public List<VideoRoomDTO> getActiveRooms() {
        // 활성 회의실 목록 반환
        return videoConfService.findAllActiveRooms();
    }

    @PostMapping("/create-room")
    public VideoRoomDTO createRoom(@RequestBody VideoRoomCreateDTO roomCreateDTO) {
        // 새 회의실 생성
        return videoConfService.createRoom(roomCreateDTO);
    }

    @GetMapping("/participants")
    public List<VideoRoomParticipantDTO> getActiveParticipants(@RequestParam String roomId) {
        return videoConfService.findActiveParticipants(roomId);
    }

    @PostMapping("/join-room")
    public ResponseEntity<String> joinRoom(@RequestBody VideoRoomJoinDTO roomJoinDTO) {
        // 회의실 참가
        boolean result = videoConfService.joinRoom(roomJoinDTO);
        if (result) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.badRequest().body("Invalid room or password");
    }

    @PostMapping("/leave-room")
    public ResponseEntity<String> leaveRoom(
            @RequestParam String roomId,
            @RequestParam String empNum) {
        // 회의실 퇴장
        videoConfService.leaveRoom(roomId, empNum);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Boolean> verifyPassword(
            @RequestParam String roomId,
            @RequestParam String password) {
        // 비밀번호 검증
        boolean isValid = videoConfService.verifyRoomPassword(roomId, password);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/check-room-validity")
    public ResponseEntity<Boolean> checkRoomValidity(@RequestParam String roomId) {
        boolean isValid = videoConfService.isRoomValid(roomId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/verify-password")
    public ResponseEntity<Boolean> verifyRoomPassword(
            @RequestParam String roomId,
            @RequestParam String password
    ) {
        boolean isValidPassword = videoConfService.verifyRoomPassword(roomId, password);
        return ResponseEntity.ok(isValidPassword);
    }
}