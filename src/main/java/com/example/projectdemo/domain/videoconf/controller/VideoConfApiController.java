package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.videoconf.dto.*;
import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videoconf")
@RequiredArgsConstructor
@Slf4j
public class VideoConfApiController {

    private final VideoConfService videoConfService;

    /**
     * 활성화된 화상회의 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<VideoRoomDTO>> getActiveRooms() {
        try {
            List<VideoRoomDTO> rooms = videoConfService.findAllActiveRooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("화상회의 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 새로운 화상회의 생성
     */
    @PostMapping("/create-room")
    public ResponseEntity<?> createRoom(@RequestBody VideoRoomCreateDTO createDTO) {
        try {
            log.info("화상회의 생성 요청: {}", createDTO);
            VideoRoomDTO createdRoom = videoConfService.createRoom(createDTO);
            return ResponseEntity.ok(createdRoom);
        } catch (Exception e) {
            log.error("화상회의 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            ));
        }
    }

    /**
     * 특정 회의실의 참가자 목록 조회
     */
    @GetMapping("/participants")
    public ResponseEntity<List<VideoRoomParticipantDTO>> getActiveParticipants(@RequestParam String roomId) {
        try {
            List<VideoRoomParticipantDTO> participants = videoConfService.findActiveParticipants(roomId);
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            log.error("참가자 목록 조회 중 오류 발생: roomId={}", roomId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 화상회의 참가
     */
    @PostMapping("/join-room")
    public ResponseEntity<?> joinRoom(@RequestBody VideoRoomJoinDTO joinDTO) {
        try {
            log.info("화상회의 참가 요청: roomId={}, empNum={}", joinDTO.getRoomId(), joinDTO.getEmpNum());
            boolean success = videoConfService.joinRoom(joinDTO);

            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("roomId", joinDTO.getRoomId());
                response.put("roomName", joinDTO.getRoomName());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "회의 참가에 실패했습니다. 비밀번호를 확인하거나 회의가 종료되었는지 확인하세요."
                ));
            }
        } catch (Exception e) {
            log.error("화상회의 참가 중 오류 발생: roomId={}, empNum={}", joinDTO.getRoomId(), joinDTO.getEmpNum(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            ));
        }
    }

    /**
     * 화상회의 퇴장
     */
    @PostMapping("/leave-room")
    public ResponseEntity<?> leaveRoom(@RequestParam String roomId, @RequestParam String empNum) {
        try {
            log.info("화상회의 퇴장 요청: roomId={}, empNum={}", roomId, empNum);
            videoConfService.leaveRoom(roomId, empNum);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("화상회의 퇴장 중 오류 발생: roomId={}, empNum={}", roomId, empNum, e);
            // 오류가 발생해도 클라이언트 측에서는 성공으로 처리하는 것이 좋음 (UI 복구)
            return ResponseEntity.ok(Map.of("success", true));
        }
    }

    /**
     * 방 유효성 체크
     */
    @GetMapping("/check-room-validity")
    public ResponseEntity<?> checkRoomValidity(@RequestParam String roomId,
                                               @RequestParam(required = false) String password) {
        try {
            log.info("방 유효성 체크 요청: roomId={}", roomId);

            // 상세 정보를 포함한 유효성 체크
            Map<String, Object> result = videoConfService.checkRoomValidityWithDetails(roomId, password);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("방 유효성 체크 중 오류 발생: roomId={}", roomId, e);
            return ResponseEntity.internalServerError().body(Map.of("valid", false, "error", true));
        }
    }

    /**
     * 비밀번호 확인
     */
    @GetMapping("/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@RequestParam String roomId, @RequestParam String password) {
        try {
            log.info("비밀번호 확인 요청: roomId={}", roomId);
            boolean isValid = videoConfService.verifyRoomPassword(roomId, password);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("비밀번호 확인 중 오류 발생: roomId={}", roomId, e);
            return ResponseEntity.ok(false);
        }
    }

    /**
     * 하트비트 처리 - 클라이언트 연결 상태 유지 및 확인
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<?> handleHeartbeat(@RequestParam String roomId, @RequestParam String empNum) {
        try {
            log.debug("하트비트 수신: roomId={}, empNum={}", roomId, empNum);
            Map<String, Object> result = videoConfService.processHeartbeat(roomId, empNum);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("하트비트 처리 중 오류 발생: roomId={}, empNum={}", roomId, empNum, e);
            return ResponseEntity.ok(Map.of("roomActive", false));
        }
    }

    /**
     * 회의실에 참가 중인 사용자 확인
     */
    @GetMapping("/check-participant")
    public ResponseEntity<?> checkParticipant(@RequestParam String roomId, @RequestParam String empNum) {
        try {
            log.info("참가자 확인 요청: roomId={}, empNum={}", roomId, empNum);
            boolean isParticipant = videoConfService.isActiveParticipant(roomId, empNum);
            return ResponseEntity.ok(Map.of("isParticipant", isParticipant));
        } catch (Exception e) {
            log.error("참가자 확인 중 오류 발생: roomId={}, empNum={}", roomId, empNum, e);
            return ResponseEntity.internalServerError().body(Map.of("isParticipant", false, "error", true));
        }
    }
}