package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {

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

    int deleteScheduleParticipant(
            @Param("scheduleId") Integer scheduleId,
            @Param("empNum") String empNum);
}