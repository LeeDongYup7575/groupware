package com.example.projectdemo.domain.videoconf.service;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.videoconf.dto.*;
import com.example.projectdemo.domain.videoconf.entity.VideoRoom;
import com.example.projectdemo.domain.videoconf.entity.VideoRoomParticipant;
import com.example.projectdemo.domain.videoconf.mapper.VideoRoomMapper;
import com.example.projectdemo.domain.videoconf.mapper.VideoRoomParticipantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoConfService {

    // 하트비트 타임아웃 (분)
    private static final long HEARTBEAT_TIMEOUT_MINUTES = 3;

    private final VideoRoomMapper videoRoomMapper;
    private final VideoRoomParticipantMapper participantMapper;
    private final EmployeesService employeesService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 모든 활성화된 화상 회의실 목록 조회
     */
    public List<VideoRoomDTO> findAllActiveRooms() {
        List<VideoRoom> rooms = videoRoomMapper.findActiveRooms();
        List<VideoRoomDTO> roomDTOs = new ArrayList<>();

        for (VideoRoom room : rooms) {
            int participantCount = participantMapper.countActiveParticipants(room.getId());

            // 방에 참가자가 없는 경우 방을 비활성화 처리
            if (participantCount == 0) {
                log.info("방 {} 에 활성 참가자가 없어 비활성화 처리합니다.", room.getId());
                videoRoomMapper.deactivateRoom(room.getId());
                continue; // 비활성화된 방은 목록에 추가하지 않음
            }

            VideoRoomDTO dto = VideoRoomDTO.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .participantsCount(participantCount)
                    .hasPassword(StringUtils.hasText(room.getPassword()))
                    .build();

            roomDTOs.add(dto);
        }

        return roomDTOs;
    }

    public void notifyParticipantsUpdate(String roomId) {
        List<VideoRoomParticipantDTO> participants = findActiveParticipants(roomId);

        WebRTCMessageDTO updateMessage = WebRTCMessageDTO.builder()
                .type("participants-update")
                .roomId(roomId)
                .payload(participants)
                .build();

        // 모든 사용자에게 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId, updateMessage);
    }


    /**
     * 현재 방에 참가 중인 모든 활성 참가자 조회
     */
    public List<VideoRoomParticipantDTO> findActiveParticipants(String roomId) {
        List<VideoRoomParticipant> participants = participantMapper.findActiveParticipantsByRoomId(roomId);

        // 참가자가 없는 경우 방 비활성화 처리
        if (participants.isEmpty()) {
            log.info("방 {} 에 활성 참가자가 없어 비활성화 처리합니다.", roomId);
            videoRoomMapper.deactivateRoom(roomId);
            return new ArrayList<>();
        }

        return participants.stream()
                .map(participant -> {
                    // 참가자의 직원 정보 조회 (직원 정보 서비스 필요)
                    EmployeesDTO employee = employeesService.findByEmpNum(participant.getEmpNum());

                    return VideoRoomParticipantDTO.builder()
                            .roomId(participant.getRoomId())
                            .empNum(participant.getEmpNum())
                            .name(employee != null ? employee.getName() : "알 수 없음")
                            .deptName(employee != null ? employee.getDepartmentName() : "")
                            .joinedAt(participant.getJoinedAt())
                            .isActive(participant.isActive())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 새 화상 회의실 생성
     */
    @Transactional
    public VideoRoomDTO createRoom(VideoRoomCreateDTO roomCreateDTO) {
        // 새 방 생성
        VideoRoom room = VideoRoom.builder()
                .id(roomCreateDTO.getId())
                .name(roomCreateDTO.getName())
                .password(roomCreateDTO.getPassword())
                .createdBy(roomCreateDTO.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .maxParticipants(10)
                .build();

        videoRoomMapper.createRoom(room);

        // 방장을 첫 참가자로 추가
        VideoRoomParticipant participant = VideoRoomParticipant.builder()
                .roomId(room.getId())
                .empNum(roomCreateDTO.getCreatedBy())
                .joinedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        participantMapper.addParticipant(participant);

        return VideoRoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .participantsCount(1)
                .hasPassword(StringUtils.hasText(room.getPassword()))
                .build();
    }

    /**
     * 회의실 참가
     */
    @Transactional
    public boolean joinRoom(VideoRoomJoinDTO roomJoinDTO) {
        // 방 존재 확인
        VideoRoom room = videoRoomMapper.findActiveRoomById(roomJoinDTO.getRoomId());

        if (room == null) {
            return false;
        }

        // 비밀번호 확인 (있는 경우)
        if (StringUtils.hasText(room.getPassword()) &&
                !room.getPassword().equals(roomJoinDTO.getRoomPassword())) {
            return false;
        }

        // 이미 참가 중인지 확인
        VideoRoomParticipant existingParticipant = participantMapper
                .findActiveParticipant(roomJoinDTO.getRoomId(), roomJoinDTO.getEmpNum());

        // 이미 참가 중이면 true 반환
        if (existingParticipant != null) {
            return true;
        }

        // 참가 가능한 최대 인원 수 확인
        int currentParticipants = participantMapper.countActiveParticipants(room.getId());
        if (currentParticipants >= room.getMaxParticipants()) {
            return false;
        }

        // 새 참가자 추가
        VideoRoomParticipant participant = VideoRoomParticipant.builder()
                .roomId(room.getId())
                .empNum(roomJoinDTO.getEmpNum())
                .joinedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        participantMapper.addParticipant(participant);

        // 참가자 업데이트 전송
        notifyParticipantsUpdate(room.getId());

        return true;
    }

    /**
     * 회의실 퇴장 처리 및 빈 방 정리
     * 강화된 오류 처리 및 로깅 추가
     */
    @Transactional
    public void leaveRoom(String roomId, String empNum) {
        if (roomId == null || empNum == null) {
            log.warn("방 퇴장 실패: roomId 또는 empNum이 null입니다. roomId={}, empNum={}", roomId, empNum);
            return;
        }

        log.info("방 퇴장 처리 시작: roomId={}, empNum={}", roomId, empNum);

        try {
            // 활성 참가자 확인
            VideoRoomParticipant participant = participantMapper.findActiveParticipant(roomId, empNum);

            if (participant == null) {
                log.info("이미 퇴장 처리된 참가자입니다: roomId={}, empNum={}", roomId, empNum);
                return;
            }

            // 참가자 비활성화
            int updated = participantMapper.deactivateParticipant(roomId, empNum, LocalDateTime.now());
            log.info("참가자 비활성화 결과: {} 행이 업데이트됨", updated);

            // 남은 참가자 확인
            int remainingParticipants = participantMapper.countActiveParticipants(roomId);
            log.info("방 {} 의 남은 활성 참가자 수: {}", roomId, remainingParticipants);

            notifyParticipantsUpdate(roomId);

            // 참가자가 없으면 방 비활성화
            if (remainingParticipants <= 0) {
                log.info("방 {} 에 활성 참가자가 없어 비활성화 처리합니다.", roomId);
                videoRoomMapper.deactivateRoom(roomId);
            }
        } catch (Exception e) {
            log.error("참가자 퇴장 처리 중 오류 발생: room={}, empNum={}, error={}", roomId, empNum, e.getMessage(), e);
            throw e; // 예외를 다시 던져서 트랜잭션 롤백 유도
        }
    }

    /**
     * 방이 유효한지 확인 및 정리 작업 수행
     */
    public boolean isRoomValid(String roomId) {
        try {
            // 1. roomId로 방 조회
            VideoRoom room = videoRoomMapper.findActiveRoomById(roomId);

            if (room == null) {
                // 방이 존재하지 않음
                log.info("방 {} 이 존재하지 않습니다.", roomId);
                return false;
            }

            // 2. 방의 상태 확인 (findActiveRoomById 메서드에서 이미 활성 상태만 조회)

            // 3. 최대 대기 시간 확인 (예: 방 생성 후 24시간 경과 시 무효)
            LocalDateTime createdAt = room.getCreatedAt();
            LocalDateTime now = LocalDateTime.now();

            if (ChronoUnit.HOURS.between(createdAt, now) > 24) {
                // 24시간 이상 경과한 방은 무효
                // 방 비활성화 처리
                log.info("방 {} 이 생성된 지 24시간이 경과하여 비활성화 처리합니다.", roomId);
                videoRoomMapper.deactivateRoom(roomId);
                return false;
            }

            // 4. 참가자 수 제한 확인
            int currentParticipantsCount = participantMapper.countActiveParticipants(roomId);

            // 실제 참가자가 없는데 방이 활성 상태인 경우 정리
            if (currentParticipantsCount <= 0) {
                log.info("방 {} 에 활성 참가자가 없어 비활성화 처리합니다.", roomId);
                videoRoomMapper.deactivateRoom(roomId);
                return false;
            }

            int maxParticipants = room.getMaxParticipants();

            if (currentParticipantsCount >= maxParticipants) {
                // 최대 참가자 수 초과
                log.info("방 {} 의 참가자 수가 최대치({})에 도달했습니다.", roomId, maxParticipants);
                return false;
            }

            return true;
        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("방 유효성 확인 중 오류 발생: {}", roomId, e);
            return false;
        }
    }

    // 방 입장 전 비밀번호 검증 메서드 추가
    public boolean verifyRoomPassword(String roomId, String password) {
        try {
            VideoRoom room = videoRoomMapper.findActiveRoomById(roomId);

            if (room == null) {
                log.info("비밀번호 검증: 방 {}이 존재하지 않습니다.", roomId);
                return false;
            }

            // 비밀번호가 없는 방이거나 입력된 비밀번호와 일치하는지 확인
            return room.getPassword() == null ||
                    room.getPassword().isEmpty() ||
                    room.getPassword().equals(password);
        } catch (Exception e) {
            log.error("방 비밀번호 검증 중 오류 발생: {}", roomId, e);
            return false;
        }
    }

    /**
     * 오래된 빈 방 정리 (스케줄러에서 호출)
     */
    @Transactional
    public void cleanupEmptyRooms() {
        try {
            log.info("빈 화상 회의실 정리 작업 시작");
            List<VideoRoom> activeRooms = videoRoomMapper.findActiveRooms();

            int cleanedCount = 0;
            for (VideoRoom room : activeRooms) {
                int participantCount = participantMapper.countActiveParticipants(room.getId());

                if (participantCount <= 0) {
                    log.info("방 {}에 활성 참가자가 없어 비활성화 처리합니다.", room.getId());
                    videoRoomMapper.deactivateRoom(room.getId());
                    cleanedCount++;
                }
            }

            log.info("빈 화상 회의실 정리 작업 완료: {} 개 방 정리됨", cleanedCount);
        } catch (Exception e) {
            log.error("빈 방 정리 작업 중 오류 발생", e);
        }
    }

    /**
     * 비활성 참가자 정리 (오래된 하트비트)
     */
    @Transactional
    public void cleanupInactiveParticipants() {
        try {
            log.info("비활성 참가자 정리 작업 시작");
            LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(HEARTBEAT_TIMEOUT_MINUTES);

            int inactiveCount = participantMapper.deactivateInactiveParticipants(null, timeoutThreshold);
            log.info("하트비트 타임아웃으로 {}명의 참가자가 비활성화되었습니다.", inactiveCount);

            // 빈 방 정리
            cleanupEmptyRooms();
        } catch (Exception e) {
            log.error("비활성 참가자 정리 중 오류 발생", e);
        }
    }

    /**
     * 하트비트 처리 및 클라이언트 연결 상태 관리
     */
    @Transactional
    public Map<String, Object> processHeartbeat(String roomId, String empNum) {
        Map<String, Object> result = new HashMap<>();
        result.put("roomActive", false);

        try {
            if (roomId == null || empNum == null) {
                return result;
            }

            // 방 존재 및 유효성 확인
            VideoRoom room = videoRoomMapper.findActiveRoomById(roomId);
            if (room == null || !isRoomStillValid(room)) {
                if (room != null) {
                    videoRoomMapper.deactivateRoom(roomId);
                }
                return result;
            }

            // 참가자 존재 확인
            VideoRoomParticipant participant = participantMapper.findActiveParticipant(roomId, empNum);

            if (participant == null) {
                // 참가자가 DB에 없는 경우 (세션이 만료되었거나 오류)
                log.warn("하트비트: 방 {}에 참가자 {}가 없습니다.", roomId, empNum);
                return result;
            }

            // 하트비트 시간 업데이트
            LocalDateTime now = LocalDateTime.now();
            participantMapper.updateHeartbeat(roomId, empNum, now);

            // 현재 참가자 수 조회
            int participantCount = participantMapper.countActiveParticipants(roomId);

            notifyParticipantsUpdate(roomId);

            // 하트비트 응답
            result.put("roomActive", true);
            result.put("participantCount", participantCount);
            result.put("timestamp", now.toString());

            return result;
        } catch (Exception e) {
            log.error("하트비트 처리 중 오류 발생: roomId={}, empNum={}", roomId, empNum, e);
            return result;
        }
    }

    /**
     * 방이 유효한지 확인 및 상세 정보 제공
     */
    public Map<String, Object> checkRoomValidityWithDetails(String roomId, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", false);

        try {
            if (roomId == null || roomId.trim().isEmpty()) {
                result.put("message", "유효하지 않은 방 ID입니다.");
                return result;
            }

            // 방 정보 조회
            VideoRoom room = videoRoomMapper.findActiveRoomById(roomId);

            // 방이 존재하지 않는 경우
            if (room == null) {
                result.put("message", "존재하지 않는 방입니다.");
                return result;
            }

            // 방 유효성 검증
            if (!isRoomStillValid(room)) {
                videoRoomMapper.deactivateRoom(roomId);
                result.put("message", "회의가 종료되었습니다.");
                return result;
            }

            // 비밀번호 확인
            if (StringUtils.hasText(room.getPassword())) {
                result.put("passwordRequired", true);

                // 비밀번호가 제공된 경우 검증
                if (password != null) {
                    if (!room.getPassword().equals(password)) {
                        result.put("passwordIncorrect", true);
                        result.put("message", "비밀번호가 일치하지 않습니다.");
                        return result;
                    }
                } else {
                    // 비밀번호가 필요하지만 제공되지 않은 경우
                    result.put("message", "비밀번호가 필요합니다.");
                    return result;
                }
            }

            // 참가자 수 제한 확인
            int currentParticipantsCount = participantMapper.countActiveParticipants(roomId);
            int maxParticipants = room.getMaxParticipants();

            if (currentParticipantsCount >= maxParticipants) {
                result.put("roomFull", true);
                result.put("message", "방이 꽉 찼습니다.");
                return result;
            }

            // 모든 검증을 통과한 경우
            result.put("valid", true);
            result.put("name", room.getName());
            result.put("participantsCount", currentParticipantsCount);
            result.put("maxParticipants", maxParticipants);

            return result;
        } catch (Exception e) {
            log.error("방 유효성 확인 중 오류 발생: {}", roomId, e);
            result.put("error", true);
            result.put("message", "서버 오류가 발생했습니다.");
            return result;
        }
    }

    /**
     * 방이 여전히 유효한지 확인
     */
    private boolean isRoomStillValid(VideoRoom room) {
        if (room == null || !room.isActive()) {
            return false;
        }

        // 방 생성 후 시간 체크 (24시간 초과 시 무효)
        LocalDateTime createdAt = room.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        // 최대 세션 유지 시간 (24시간)
        long MAX_ROOM_DURATION_HOURS = 24;
        return ChronoUnit.HOURS.between(createdAt, now) <= MAX_ROOM_DURATION_HOURS;
    }

    /**
     * 특정 사용자가 회의실에 활성화된 참가자인지 확인
     */
    public boolean isActiveParticipant(String roomId, String empNum) {
        if (roomId == null || empNum == null) {
            return false;
        }

        try {
            // 활성 참가자 조회
            VideoRoomParticipant participant = participantMapper.findActiveParticipant(roomId, empNum);
            return participant != null;
        } catch (Exception e) {
            log.error("참가자 확인 중 오류 발생: roomId={}, empNum={}", roomId, empNum, e);
            return false;
        }
    }
}