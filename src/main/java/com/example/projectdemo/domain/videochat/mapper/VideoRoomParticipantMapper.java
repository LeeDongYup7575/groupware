package com.example.projectdemo.domain.videochat.mapper;

import com.example.projectdemo.domain.videochat.dto.VideoRoomParticipantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VideoRoomParticipantMapper {
    // 특정 회의실의 모든 활성 참가자 조회
    List<VideoRoomParticipantDTO> findActiveParticipantsByRoomId(@Param("roomId") String roomId);

    // 새로운 참가자 추가
    int addParticipant(VideoRoomParticipantDTO participantDTO);

    // 참가자 퇴장 처리
    int updateParticipantLeftAt(@Param("id") Long id,
                                @Param("leftAt") LocalDateTime leftAt,
                                @Param("isActive") boolean isActive);

    // 특정 회의실과 사원번호로 참가자 조회
    VideoRoomParticipantDTO findParticipantByRoomIdAndEmpNum(@Param("roomId") String roomId,
                                                             @Param("empNum") String empNum);

    // 하트비트 업데이트 - 연결상태 확인용이니 윤진언니 자꾸 2PM 리슨투마하트비트 금지
    int updateHeartbeat(@Param("id") Long id, @Param("lastHeartbeat") LocalDateTime lastHeartbeat);

    // 오래된 하트비트를 가진 참가자 조회 (비활성화 대상)
    List<VideoRoomParticipantDTO> findInactiveParticipants(@Param("threshold") LocalDateTime threshold);

    // 특정 회의실의 참가자 수 조회
    int countActiveParticipantsByRoomId(@Param("roomId") String roomId);

    // 특정 사원이 참여중인 활성 회의실 조회
    List<String> findActiveRoomsByEmpNum(@Param("empNum") String empNum);

    // 특정 회의실의 참가자 비활성화
    int deactivateParticipantsByRoomId(@Param("roomId") String roomId);

    // 하트비트가 오래된 참가자 비활성화
    int deactivateOldParticipants(@Param("threshold") LocalDateTime threshold);
}