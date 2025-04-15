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

    // 사원 관련 일정 조회 (생성한 일정 + 참여하는 일정)
    List<ScheduleDTO> findSchedulesByEmpNum(@Param("empNum") String empNum);

    ScheduleDTO selectScheduleById(@Param("id") Integer id);

    List<ScheduleDTO> selectSchedulesByProject(@Param("projectId") Integer projectId);

    // selectSchedulesByEmployee는 findSchedulesByEmpNum과 기능이 중복되어 보입니다
    // 필요하다면 용도 차이를 명확히 해야 합니다
    List<ScheduleDTO> selectSchedulesByEmployee(@Param("empNum") String empNum);

    List<ScheduleDTO> selectSchedulesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 반복 타입별 일정 조회 (새로 추가)
    List<ScheduleDTO> selectSchedulesByRepeatType(@Param("repeatType") String repeatType);

    // 일정 관리 관련 메서드
    int insertSchedule(ScheduleDTO schedule);
    int updateSchedule(ScheduleDTO schedule);
    int deleteSchedule(@Param("id") Integer id);

    // 알림 관련 일정 조회 (새로 추가)
    List<ScheduleDTO> selectSchedulesByNotificationType(
            @Param("notificationType") String notificationType);

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