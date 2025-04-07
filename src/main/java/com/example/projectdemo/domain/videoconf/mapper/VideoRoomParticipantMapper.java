package com.example.projectdemo.domain.videoconf.mapper;

import com.example.projectdemo.domain.videoconf.entity.VideoRoomParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VideoRoomParticipantMapper {

    List<VideoRoomParticipant> findActiveParticipantsByRoomId(@Param("roomId") String roomId);

    VideoRoomParticipant findActiveParticipant(@Param("roomId") String roomId, @Param("empNum") String empNum);

    int countActiveParticipants(@Param("roomId") String roomId);

    int addParticipant(VideoRoomParticipant participant);

    int deactivateParticipant(@Param("roomId") String roomId, @Param("empNum") String empNum, @Param("leftAt") LocalDateTime leftAt);
}