package com.example.projectdemo.domain.booking.mapper;

import com.example.projectdemo.domain.booking.entity.MeetingRoom;
import com.example.projectdemo.domain.booking.entity.MeetingRoomBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingRoomMapper {

    List<MeetingRoom> findAllMeetingRooms();

    MeetingRoom findMeetingRoomById(Integer id);

    List<MeetingRoomBooking> findAllMeetingRoomBookings();

    List<MeetingRoomBooking> findMeetingRoomBookingsByEmpNum(String empNum);

    List<MeetingRoomBooking> findMeetingRoomBookingsByRoomId(Integer roomId);

    List<MeetingRoomBooking> findMeetingRoomBookingsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    int insertMeetingRoomBooking(MeetingRoomBooking booking);

    int updateMeetingRoomBooking(MeetingRoomBooking booking);

    int cancelMeetingRoomBooking(Integer id);

    boolean isRoomAvailable(
            @Param("roomId") Integer roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId);
}