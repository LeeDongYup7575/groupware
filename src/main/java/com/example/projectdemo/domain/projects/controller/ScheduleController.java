package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.service.ProjectService;
import com.example.projectdemo.domain.projects.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/workmanagement/schedule")
public class ScheduleController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 일정 관리 페이지
     */
    @GetMapping
    public String scheduleMain(HttpServletRequest request,
                               @RequestParam(required = false) Integer projectId,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) {
            return "redirect:/auth/login";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
        if (employee == null) {
            return "redirect:/auth/login";
        }

        // 현재 날짜 설정 (날짜가 지정되지 않은 경우)
        if (date == null) {
            date = LocalDate.now();
        }

        // 월의 시작일과 종료일 계산
        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(23, 59, 59);

        // 프로젝트 목록 조회
        List<ProjectDTO> accessibleProjects = projectService.getAccessibleProjects(empNum);

        // 일정 목록 조회 (프로젝트 ID가 지정된 경우 해당 프로젝트 일정만 조회)
        List<ScheduleDTO> schedules;
        if (projectId != null) {
            schedules = scheduleService.getSchedulesByProject(projectId);
        } else {
            schedules = scheduleService.getSchedulesByDateRange(startDateTime, endDateTime);
        }

        model.addAttribute("employee", employee);
        model.addAttribute("accessibleProjects", accessibleProjects);
        model.addAttribute("selectedProjectId", projectId);
        model.addAttribute("currentDate", date);
        model.addAttribute("firstDayOfMonth", firstDayOfMonth);
        model.addAttribute("lastDayOfMonth", lastDayOfMonth);
        model.addAttribute("schedules", schedules);

        return "projects/schedule";
    }

    /**
     * 일정 상세 조회
     */
    @GetMapping("/{scheduleId}")
    @ResponseBody
    public ResponseEntity<ScheduleDTO> getScheduleDetail(@PathVariable Integer scheduleId) {
        ScheduleDTO schedule = scheduleService.getScheduleById(scheduleId);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    /**
     * 프로젝트별 일정 목록 조회 (AJAX)
     */
    @GetMapping("/project/{projectId}")
    @ResponseBody
    public ResponseEntity<List<ScheduleDTO>> getProjectSchedules(@PathVariable Integer projectId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByProject(projectId);
        return ResponseEntity.ok(schedules);
    }

    /**
     * 개인 일정 목록 조회 (AJAX)
     */
    @GetMapping("/employee")
    @ResponseBody
    public ResponseEntity<List<ScheduleDTO>> getEmployeeSchedules(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByEmployee(empNum);
        return ResponseEntity.ok(schedules);
    }

    /**
     * 특정 기간의 일정 목록 조회 (AJAX)
     */
    @GetMapping("/range")
    @ResponseBody
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByDateRange(start, end);
        return ResponseEntity.ok(schedules);
    }
}