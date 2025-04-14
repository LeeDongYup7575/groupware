package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.*;
import com.example.projectdemo.domain.projects.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/workmanagement/dashboard")
public class WorkDashboardController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TaskStatisticsService taskStatisticsService;

    /**
     * 업무 대시보드 메인 페이지
     */
    @GetMapping
    public String dashboardMain(HttpServletRequest request, Model model) {
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

        // 1. 직원이 참여중인 프로젝트 목록 (최대 5개)
        List<ProjectDTO> recentProjects = projectService.getProjectsByEmployee(empNum);
        if (recentProjects.size() > 5) {
            recentProjects = recentProjects.subList(0, 5);
        }

        // 2. 직원의 담당 업무 목록 (최대 10개)
        List<TaskDTO> assignedTasks = taskService.getTasksByAssignee(empNum);
        if (assignedTasks.size() > 10) {
            assignedTasks = assignedTasks.subList(0, 10);
        }

        // 3. 오늘 할 일 목록
        List<TodoDTO> todayTodos = todoService.getTodosByDate(empNum, LocalDate.now());

        // 4. 오늘 및 다가오는 일정 (7일 이내)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        List<ScheduleDTO> upcomingSchedules = scheduleService.getSchedulesByDateRange(now, sevenDaysLater);

        // 5. 업무 통계 정보
        TaskSummaryDTO taskSummary = taskStatisticsService.getEmployeeTaskSummary(empNum);

        model.addAttribute("employee", employee);
        model.addAttribute("recentProjects", recentProjects);
        model.addAttribute("assignedTasks", assignedTasks);
        model.addAttribute("todayTodos", todayTodos);
        model.addAttribute("upcomingSchedules", upcomingSchedules);
        model.addAttribute("taskSummary", taskSummary);

        return "projects/dashboard";
    }
}