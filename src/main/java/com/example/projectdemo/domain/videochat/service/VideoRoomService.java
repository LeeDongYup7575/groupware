package com.example.projectdemo.domain.videochat.service;

import com.example.projectdemo.domain.videochat.dto.VideoRoomDTO;
import com.example.projectdemo.domain.videochat.dto.VideoRoomParticipantDTO;
import com.example.projectdemo.domain.videochat.mapper.VideoRoomMapper;
import com.example.projectdemo.domain.videochat.mapper.VideoRoomParticipantMapper;
import com.example.projectdemo.domain.videochat.util.SimplePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VideoRoomService {

    @Autowired
    private VideoRoomMapper videoRoomMapper;

    @Autowired
    private VideoRoomParticipantMapper participantMapper;

    private final SimplePasswordEncoder passwordEncoder = new SimplePasswordEncoder();

    // 모든 활성화된 화상회의실 조회
    public List<VideoRoomDTO> getAllActiveRooms() {
        return videoRoomMapper.findAllActiveRooms();
    }

    // 회의실 이름으로 검색
    public List<VideoRoomDTO> searchRoomsByName(String name) {
        return videoRoomMapper.searchRoomsByName(name);
    }

    // 특정 ID의 회의실 조회
    public VideoRoomDTO getRoomById(String id) {
        return videoRoomMapper.findRoomById(id);
    }

    // 새로운 회의실 생성
    @Transactional
    public VideoRoomDTO createRoom(VideoRoomDTO roomDTO) {
        // UUID 생성
        roomDTO.setId(UUID.randomUUID().toString());

        // 비밀번호가 있다면 암호화
        if (roomDTO.getPassword() != null && !roomDTO.getPassword().isEmpty()) {
            roomDTO.setPassword(passwordEncoder.encode(roomDTO.getPassword()));
        }

        videoRoomMapper.createRoom(roomDTO);
        return roomDTO;
    }

    // 회의실 비밀번호 확인
    public boolean checkRoomPassword(String roomId, String password) {
        String storedPassword = videoRoomMapper.getPasswordById(roomId);

        // 비밀번호가 없는 방이면 항상 통과
        if (storedPassword == null || storedPassword.isEmpty()) {
            return true;
        }

        // 비밀번호 검증
        return passwordEncoder.matches(password, storedPassword);
    }

    // 회의실 활성 상태 변경
    @Transactional
    public void updateRoomActiveStatus(String roomId, boolean isActive) {
        videoRoomMapper.updateRoomActiveStatus(roomId, isActive);

        // 회의실을 비활성화할 경우 모든 참가자도 비활성화
        if (!isActive) {
            participantMapper.deactivateParticipantsByRoomId(roomId);
        }
    }

    // 회의실 참가자 추가
    @Transactional
    public VideoRoomParticipantDTO joinRoom(String roomId, String empNum) {
        // 이미 참가중인지 확인
        VideoRoomParticipantDTO existingParticipant = participantMapper.findParticipantByRoomIdAndEmpNum(roomId, empNum);

        // 이미 참가중이면 하트비트만 업데이트
        if (existingParticipant != null) {
            participantMapper.updateHeartbeat(existingParticipant.getId(), LocalDateTime.now());
            return existingParticipant;
        }

        // 새로운 참가자 추가
        VideoRoomParticipantDTO newParticipant = new VideoRoomParticipantDTO();
        newParticipant.setRoomId(roomId);
        newParticipant.setEmpNum(empNum);
        newParticipant.setActive(true);

        participantMapper.addParticipant(newParticipant);

        // 추가 후 전체 정보 다시 조회
        return participantMapper.findParticipantByRoomIdAndEmpNum(roomId, empNum);
    }

    // 회의실 나가기
    @Transactional
    public void leaveRoom(String roomId, String empNum) {
        VideoRoomParticipantDTO participant = participantMapper.findParticipantByRoomIdAndEmpNum(roomId, empNum);

        if (participant != null) {
            participantMapper.updateParticipantLeftAt(participant.getId(), LocalDateTime.now(), false);

            // 참가자가 없으면 방도 비활성화 처리
            int remainingParticipants = participantMapper.countActiveParticipantsByRoomId(roomId);
            if (remainingParticipants == 0) {
                videoRoomMapper.updateRoomActiveStatus(roomId, false);
            }
        }
    }

    // 하트비트 업데이트
    public void updateHeartbeat(String roomId, String empNum) {
        VideoRoomParticipantDTO participant = participantMapper.findParticipantByRoomIdAndEmpNum(roomId, empNum);

        if (participant != null) {
            participantMapper.updateHeartbeat(participant.getId(), LocalDateTime.now());
        }
    }

    // 회의실 참가자 목록 조회
    public List<VideoRoomParticipantDTO> getRoomParticipants(String roomId) {
        return participantMapper.findActiveParticipantsByRoomId(roomId);
    }

    // 주기적으로 오래된 하트비트를 가진 참가자 정리 (30초마다 실행)
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void cleanupInactiveParticipants() {
        // 1분 이상 하트비트가 없으면 비활성화
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);

        // 오래된 참가자 비활성화
        participantMapper.deactivateOldParticipants(threshold);

        // 참가자가 없는 회의실 목록 조회
        List<String> emptyRooms = videoRoomMapper.findEmptyRooms();

        // 참가자가 없는 회의실 비활성화
        for (String roomId : emptyRooms) {
            videoRoomMapper.updateRoomActiveStatus(roomId, false);
        }
    }

    // 특정 사용자가 현재 참여중인 회의실 확인
    public List<String> getActiveRoomsByEmpNum(String empNum) {
        return participantMapper.findActiveRoomsByEmpNum(empNum);
    }

    // 회의실 현재 참가자 수 확인
    public int getCurrentParticipantsCount(String roomId) {
        return participantMapper.countActiveParticipantsByRoomId(roomId);
    }
}
