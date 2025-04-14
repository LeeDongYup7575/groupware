package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.dto.ScheduleParticipantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {

    // 일정 조회 관련 메서드
    List<ScheduleDTO> findAllSchedules();
    List<ScheduleDTO> findSchedulesByEmpNum(@Param("empNum") String empNum);
    ScheduleDTO selectScheduleById(@Param("id") Integer id);
    List<ScheduleDTO> selectSchedulesByProject(@Param("projectId") Integer projectId);
    List<ScheduleDTO> selectSchedulesByEmployee(@Param("empNum") String empNum);
    List<ScheduleDTO> selectSchedulesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 일정 관리 관련 메서드
    int insertSchedule(ScheduleDTO schedule);
    int updateSchedule(ScheduleDTO schedule);
    int deleteSchedule(@Param("id") Integer id);

    // 일정 참여자 관련 메서드
    List<ScheduleParticipantDTO> selectScheduleParticipants(@Param("scheduleId") Integer scheduleId);
    ScheduleParticipantDTO selectScheduleParticipant(
            @Param("scheduleId") Integer scheduleId,
            @Param("empNum") String empNum);
    int insertScheduleParticipant(ScheduleParticipantDTO participant);
    int updateParticipantStatus(
            @Param("scheduleId") Integer scheduleId,
            @Param("empNum") String empNum,
            @Param("status") String status);
    int deleteScheduleParticipant(
            @Param("scheduleId") Integer scheduleId,
            @Param("empNum") String empNum);
}