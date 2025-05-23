package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

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
        // 기본값 설정 (필요시)
        if (schedule.getRepeatType() == null) {
            schedule.setRepeatType("없음");
        }
        if (schedule.getColor() == null) {
            schedule.setColor("#3498db");
        }

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

    public void removeScheduleParticipant(Integer scheduleId, String empNum) {
        scheduleMapper.deleteScheduleParticipant(scheduleId, empNum);
    }
}