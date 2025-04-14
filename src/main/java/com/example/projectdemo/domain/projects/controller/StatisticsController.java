package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import com.example.projectdemo.domain.projects.dto.TaskSummaryDTO;
import com.example.projectdemo.domain.projects.service.ProjectService;
import com.example.projectdemo.domain.projects.service.TaskLogService;
import com.example.projectdemo.domain.projects.service.TaskService;
import com.example.projectdemo.domain.projects.service.TaskStatisticsService;
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
@RequestMapping("/workmanagement/statistics")
public class StatisticsController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private TaskStatisticsService taskStatisticsService;

    /**
     * 업무 통계 메인 페이지
     */
    @GetMapping
    public String statisticsMain(HttpServletRequest request,
                                 @RequestParam(required = false) Integer projectId,
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

        // 직원이 접근 가능한 프로젝트 목록 조회
        List<ProjectDTO> accessibleProjects = projectService.getAccessibleProjects(empNum);

        // 통계 데이터 조회
        TaskSummaryDTO personalTaskSummary = taskStatisticsService.getEmployeeTaskSummary(empNum);

        TaskSummaryDTO projectTaskSummary = null;
        if (projectId != null) {
            projectTaskSummary = taskStatisticsService.getProjectTaskSummary(projectId);
            model.addAttribute("selectedProject", projectService.getProjectById(projectId));
        }

        // 최근 한 달간의 로그 조회
        LocalDateTime startDate = LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now();
        List<TaskLogDTO> recentLogs = taskLogService.getTaskLogsByDateRange(startDate, endDate);

        model.addAttribute("employee", employee);
        model.addAttribute("accessibleProjects", accessibleProjects);
        model.addAttribute("selectedProjectId", projectId);
        model.addAttribute("personalTaskSummary", personalTaskSummary);
        model.addAttribute("projectTaskSummary", projectTaskSummary);
        model.addAttribute("recentLogs", recentLogs);

        return "projects/statistics";
    }

    /**
     * 프로젝트 업무 통계 조회 (AJAX)
     */
    @GetMapping("/api/project/{projectId}")
    @ResponseBody
    public ResponseEntity<TaskSummaryDTO> getProjectStatistics(@PathVariable Integer projectId) {
        TaskSummaryDTO summary = taskStatisticsService.getProjectTaskSummary(projectId);
        return ResponseEntity.ok(summary);
    }

    /**
     * 개인 업무 통계 조회 (AJAX)
     */
    @GetMapping("/api/employee")
    @ResponseBody
    public ResponseEntity<TaskSummaryDTO> getEmployeeStatistics(HttpServletRequest request) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        TaskSummaryDTO summary = taskStatisticsService.getEmployeeTaskSummary(empNum);
        return ResponseEntity.ok(summary);
    }

    /**
     * 특정 기간의 업무 로그 조회 (AJAX)
     */
    @GetMapping("/api/logs")
    @ResponseBody
    public ResponseEntity<List<TaskLogDTO>> getTaskLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TaskLogDTO> logs = taskLogService.getTaskLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(logs);
    }
}