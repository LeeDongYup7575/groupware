package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.dto.ScheduleParticipantDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {

    /**
     * 프로젝트별 일정 목록 조회
     */
    List<ScheduleDTO> getSchedulesByProject(Integer projectId);

    /**
     * 직원별 일정 목록 조회
     */
    List<ScheduleDTO> getSchedulesByEmployee(String empNum);

    /**
     * 특정 기간의 일정 목록 조회
     */
    List<ScheduleDTO> getSchedulesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 일정 상세 조회
     */
    ScheduleDTO getScheduleById(Integer id);

    /**
     * 신규 일정 등록
     */
    ScheduleDTO createSchedule(ScheduleDTO schedule);

    /**
     * 일정 정보 업데이트
     */
    ScheduleDTO updateSchedule(ScheduleDTO schedule);

    /**
     * 일정 삭제
     */
    void deleteSchedule(Integer id);

    /**
     * 일정 참석자 목록 조회
     */
    List<ScheduleParticipantDTO> getScheduleParticipants(Integer scheduleId);

    /**
     * 일정 참석자 추가
     */
    ScheduleParticipantDTO addScheduleParticipant(Integer scheduleId, String empNum);

    /**
     * 일정 참석 상태 업데이트
     */
    ScheduleParticipantDTO updateParticipantStatus(Integer scheduleId, String empNum, String status);

    /**
     * 일정 참석자 제거
     */
    void removeScheduleParticipant(Integer scheduleId, String empNum);
}