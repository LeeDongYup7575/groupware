package com.example.projectdemo.domain.videochat.mapper;

import com.example.projectdemo.domain.videochat.dto.VideoRoomDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoRoomMapper {
    // 모든 활성화된 화상회의실 조회
    List<VideoRoomDTO> findAllActiveRooms();

    // 특정 ID의 화상회의실 조회
    VideoRoomDTO findRoomById(@Param("id") String id);

    // 이름으로 화상회의실 검색
    List<VideoRoomDTO> searchRoomsByName(@Param("name") String name);

    // 새로운 화상회의실 생성
    int createRoom(VideoRoomDTO roomDTO);

    // 화상회의실 정보 업데이트
    int updateRoom(VideoRoomDTO roomDTO);

    // 화상회의실 활성 상태 변경
    int updateRoomActiveStatus(@Param("id") String id, @Param("isActive") boolean isActive);

    // 참가자가 없는 회의실 조회
    List<String> findEmptyRooms();

    // 비밀번호 확인
    String getPasswordById(@Param("id") String id);

    // 현재 참가자 수 조회
    int countCurrentParticipants(@Param("roomId") String roomId);
}