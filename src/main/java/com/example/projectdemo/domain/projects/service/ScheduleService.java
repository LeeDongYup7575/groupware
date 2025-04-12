package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.dto.ScheduleParticipantDTO;

import java.time.LocalDateTime;
import java.util.List;

import com.example.projectdemo.domain.projects.mapper.ScheduleMapper;
import com.example.projectdemo.domain.projects.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService{

    @Autowired
    private final ScheduleMapper scheduleMapper;

    public List<ScheduleDTO> getSchedulesByProject(Integer projectId) {
        return scheduleMapper.selectSchedulesByProject(projectId);
    }

    public List<ScheduleDTO> getSchedulesByEmployee(String empNum) {
        return scheduleMapper.selectSchedulesByEmployee(empNum);
    }

    public List<ScheduleDTO> getSchedulesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.selectSchedulesByDateRange(startDate, endDate);
    }

    public ScheduleDTO getScheduleById(Integer id) {
        return scheduleMapper.selectScheduleById(id);
    }

    public ScheduleDTO createSchedule(ScheduleDTO schedule) {
        scheduleMapper.insertSchedule(schedule);
        return schedule;
    }

    public ScheduleDTO updateSchedule(ScheduleDTO schedule) {
        scheduleMapper.updateSchedule(schedule);
        return schedule;
    }

    public void deleteSchedule(Integer id) {
        scheduleMapper.deleteSchedule(id);
    }

    public List<ScheduleParticipantDTO> getScheduleParticipants(Integer scheduleId) {
        return scheduleMapper.selectScheduleParticipants(scheduleId);
    }

    public ScheduleParticipantDTO addScheduleParticipant(Integer scheduleId, String empNum) {
        ScheduleParticipantDTO dto = ScheduleParticipantDTO.builder()
                .scheduleId(scheduleId)
                .empNum(empNum)
                .status("대기") // 기본 상태
                .build();

        scheduleMapper.insertScheduleParticipant(dto);
        return dto;
    }

    public ScheduleParticipantDTO updateParticipantStatus(Integer scheduleId, String empNum, String status) {
        scheduleMapper.updateParticipantStatus(scheduleId, empNum, status);
        return scheduleMapper.selectScheduleParticipant(scheduleId, empNum);
    }


    public void removeScheduleParticipant(Integer scheduleId, String empNum) {
        scheduleMapper.deleteScheduleParticipant(scheduleId, empNum);
    }
}
