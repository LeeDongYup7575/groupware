package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByProject(projectId));
    }

    @GetMapping("/employee")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByEmployee(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(scheduleService.getSchedulesByEmployee(empNum));
    }

    @GetMapping("/range")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDateRange(start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Integer id) {
        ScheduleDTO schedule = scheduleService.getScheduleById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(
            @PathVariable Integer id,
            @RequestBody ScheduleDTO schedule) {
        schedule.setId(id);
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(schedule);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/participants/{empNum}")
    public ResponseEntity<?> removeScheduleParticipant(
            @PathVariable Integer id,
            @PathVariable String empNum) {
        scheduleService.removeScheduleParticipant(id, empNum);
        return ResponseEntity.ok().build();
    }
}