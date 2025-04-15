package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskLogDTO;
import com.example.projectdemo.domain.projects.service.TaskLogService;
import com.example.projectdemo.domain.projects.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/logs")
public class TaskLogController {

    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private TaskService taskService;

    /**
     * 최근 업무 로그 조회
     */
    @GetMapping("/recent")
    public ResponseEntity<List<TaskLogDTO>> getRecentTaskLogs(
            @RequestParam(defaultValue = "0") int limit) {
        List<TaskLogDTO> logs = taskLogService.getRecentTaskLogs(limit);

        // 로그에 필요한 업무 타이틀과 프로젝트명 정보 추가
        logs.forEach(log -> {
            if (log.getTaskTitle() == null && log.getTaskId() != null) {
                TaskDTO task = taskService.getTaskById(log.getTaskId());
                if (task != null) {
                    log.setTaskTitle(task.getTitle());
                    log.setProjectName(task.getProjectName());
                }
            }

            // 사원 이름 정보가 없는 경우 조회
            if (log.getEmpName() == null && log.getEmpNum() != null) {
                EmployeesDTO employee = employeesService.findByEmpNum(log.getEmpNum());
                if (employee != null) {
                    log.setEmpName(employee.getName());
                }
            }
        });

        return ResponseEntity.ok(logs);
    }

    /**
     * 특정 사원의 업무 로그 조회
     */
    @GetMapping("/employee")
    public ResponseEntity<List<TaskLogDTO>> getTaskLogsByEmployee(
            HttpServletRequest request,
            @RequestParam(defaultValue = "20") int limit) {
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(taskLogService.getTaskLogsByEmployee(empNum, limit));
    }

    /**
     * 특정 프로젝트의 업무 로그 조회
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskLogDTO>> getTaskLogsByProject(
            @PathVariable Integer projectId,
            @RequestParam(defaultValue = "50") int limit) {
        List<TaskLogDTO> logs = taskLogService.getTaskLogsByProject(projectId, limit);

        // 로그에 필요한 업무 타이틀과 프로젝트명 정보 추가
        logs.forEach(log -> {
            if (log.getTaskTitle() == null && log.getTaskId() != null) {
                TaskDTO task = taskService.getTaskById(log.getTaskId());
                if (task != null) {
                    log.setTaskTitle(task.getTitle());
                    log.setProjectName(task.getProjectName());
                }
            }

            // 사원 이름 정보가 없는 경우 조회
            if (log.getEmpName() == null && log.getEmpNum() != null) {
                EmployeesDTO employee = employeesService.findByEmpNum(log.getEmpNum());
                if (employee != null) {
                    log.setEmpName(employee.getName());
                }
            }
        });

        return ResponseEntity.ok(logs);
    }

    /**
     * 특정 기간 내의 업무 로그 조회
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<TaskLogDTO>> getTaskLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(taskLogService.getTaskLogsByDateRange(startDate, endDate));
    }
}