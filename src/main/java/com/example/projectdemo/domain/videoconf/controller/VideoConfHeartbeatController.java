package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 화상 회의 연결 상태를 모니터링하기 위한 하트비트 컨트롤러
 */
@RestController
@RequestMapping("/api/videoconf/heartbeat")
@RequiredArgsConstructor
@Slf4j
public class VideoConfHeartbeatController {

    private final VideoConfService videoConfService;

    /**
     * 클라이언트에서 주기적으로 호출하여 참가자가 여전히 활성 상태임을 알림
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> heartbeat(
            @RequestParam String roomId,
            @RequestParam String empNum) {

        log.debug("하트비트 수신: roomId={}, empNum={}", roomId, empNum);
        Map<String, Object> response = new HashMap<>();

        // 방 유효성 검증
        boolean isRoomValid = videoConfService.isRoomValid(roomId);

        response.put("roomActive", isRoomValid);

        // 방이 활성화되어 있지 않다면 클라이언트에게 알림
        if (!isRoomValid) {
            log.info("유효하지 않은 방에 대한 하트비트: roomId={}, empNum={}", roomId, empNum);
            return ResponseEntity.ok(response);
        }

        // 참가자 수 정보 추가
        int participantCount = videoConfService.findActiveParticipants(roomId).size();
        response.put("participantCount", participantCount);

        return ResponseEntity.ok(response);
    }
}