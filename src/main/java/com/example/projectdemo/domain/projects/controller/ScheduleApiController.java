package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.dto.ScheduleParticipantDTO;
import com.example.projectdemo.domain.projects.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 프로젝트별 일정 목록 조회
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByProject(projectId));
    }

    /**
     * 직원별 일정 목록 조회
     */
    @GetMapping("/employee")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByEmployee(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(scheduleService.getSchedulesByEmployee(empNum));
    }

    /**
     * 특정 기간의 일정 목록 조회
     */
    @GetMapping("/range")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDateRange(start, end));
    }

    /**
     * 일정 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Integer id) {
        ScheduleDTO schedule = scheduleService.getScheduleById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    /**
     * 새 일정 등록
     */
    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(
            @RequestBody ScheduleDTO schedule,
            HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        schedule.setCreatorEmpNum(empNum);
        ScheduleDTO createdSchedule = scheduleService.createSchedule(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    /**
     * 일정 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(
            @PathVariable Integer id,
            @RequestBody ScheduleDTO schedule) {
        schedule.setId(id);
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(schedule);
        return ResponseEntity.ok(updatedSchedule);
    }

    /**
     * 일정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 일정 참석자 목록 조회
     */
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ScheduleParticipantDTO>> getScheduleParticipants(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleParticipants(id));
    }

    /**
     * 일정 참석자 추가
     */
    @PostMapping("/{id}/participants")
    public ResponseEntity<ScheduleParticipantDTO> addScheduleParticipant(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String empNum = body.get("empNum");
        if (empNum == null || empNum.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ScheduleParticipantDTO participant = scheduleService.addScheduleParticipant(id, empNum);
        return ResponseEntity.status(HttpStatus.CREATED).body(participant);
    }

    /**
     * 일정 참석 상태 업데이트
     */
    @PutMapping("/{id}/participants/{empNum}")
    public ResponseEntity<ScheduleParticipantDTO> updateParticipantStatus(
            @PathVariable Integer id,
            @PathVariable String empNum,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ScheduleParticipantDTO participant = scheduleService.updateParticipantStatus(id, empNum, status);
        return ResponseEntity.ok(participant);
    }

    /**
     * 일정 참석자 제거
     */
    @DeleteMapping("/{id}/participants/{empNum}")
    public ResponseEntity<?> removeScheduleParticipant(
            @PathVariable Integer id,
            @PathVariable String empNum) {
        scheduleService.removeScheduleParticipant(id, empNum);
        return ResponseEntity.ok().build();
    }
}