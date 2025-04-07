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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;



import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoConfService {

    private final VideoRoomMapper videoRoomMapper;
    private final VideoRoomParticipantMapper participantMapper;
    private final EmployeesService employeesService;


    /**
     * 모든 활성화된 화상 회의실 목록 조회
     */
    public List<VideoRoomDTO> findAllActiveRooms() {
        List<VideoRoom> rooms = videoRoomMapper.findActiveRooms();
        List<VideoRoomDTO> roomDTOs = new ArrayList<>();

        for (VideoRoom room : rooms) {
            int participantCount = participantMapper.countActiveParticipants(room.getId());

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

    /**
     * 현재 방에 참가 중인 모든 활성 참가자 조회
     */
    public List<VideoRoomParticipantDTO> findActiveParticipants(String roomId) {
        List<VideoRoomParticipant> participants = participantMapper.findActiveParticipantsByRoomId(roomId);

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

        return true;
    }

    @Transactional
    public void leaveRoom(String roomId, String empNum) {
        // 이미 비활성화된 참가자인지 확인하기 위해 SELECT 먼저 수행
        VideoRoomParticipant existingParticipant = participantMapper.findActiveParticipant(roomId, empNum);

        // 활성 상태인 참가자가 있을 경우에만 비활성화 진행
        if (existingParticipant != null) {
            try {
                // 참가자 비활성화
                participantMapper.deactivateParticipant(roomId, empNum, LocalDateTime.now());

                // 남은 참가자 수 확인
                int remainingParticipants = participantMapper.countActiveParticipants(roomId);

                // 참가자가 없으면 방 비활성화
                if (remainingParticipants == 0) {
                    videoRoomMapper.deactivateRoom(roomId);
                }
            } catch (Exception e) {
                // 중복 비활성화 시도나 다른 오류 방지를 위한 예외 처리
                log.warn("참가자 비활성화 중 오류 발생: room={}, empNum={}, error={}", roomId, empNum, e.getMessage());
            }
        } else {
            log.info("이미 비활성화된 참가자입니다: room={}, empNum={}", roomId, empNum);
        }
    }

    public boolean isRoomValid(String roomId) {
        try {
            // 1. roomId로 방 조회
            VideoRoom room = videoRoomMapper.findActiveRoomById(roomId);

            if (room == null) {
                // 방이 존재하지 않음
                return false;
            }

            // 2. 방의 상태 확인 (findActiveRoomById 메서드에서 이미 활성 상태만 조회)

            // 3. 최대 대기 시간 확인 (예: 방 생성 후 24시간 경과 시 무효)
            LocalDateTime createdAt = room.getCreatedAt();
            LocalDateTime now = LocalDateTime.now();

            if (ChronoUnit.HOURS.between(createdAt, now) > 24) {
                // 24시간 이상 경과한 방은 무효
                // 방 비활성화 처리
                videoRoomMapper.deactivateRoom(roomId);
                return false;
            }

            // 4. 참가자 수 제한 확인
            int currentParticipantsCount = participantMapper.countActiveParticipants(roomId);
            int maxParticipants = room.getMaxParticipants();

            if (currentParticipantsCount >= maxParticipants) {
                // 최대 참가자 수 초과
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
}