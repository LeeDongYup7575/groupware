package com.example.projectdemo.domain.videoconf.mapper;

import com.example.projectdemo.domain.videoconf.entity.VideoRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoRoomMapper {

    List<VideoRoom> findActiveRooms();

    VideoRoom findActiveRoomById(@Param("roomId") String roomId);

    List<VideoRoom> findPublicRooms();

    List<VideoRoom> findRoomsByCreator(@Param("empNum") String empNum);

    int createRoom(VideoRoom room);

    int deactivateRoom(@Param("roomId") String roomId);
}