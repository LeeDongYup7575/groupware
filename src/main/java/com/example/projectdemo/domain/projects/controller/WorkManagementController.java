package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.DepartmentsService;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.*;
import com.example.projectdemo.domain.projects.service.*;
import com.example.projectdemo.util.BaseController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/workmanagement")
public class WorkManagementController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private DepartmentsService departmentsService;

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private TaskStatisticsService taskStatisticsService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 업무 관리 메인 페이지 (업무 탭)
     */
    @GetMapping
    public String workManagementMain(HttpServletRequest request, Model model) {
        EmployeesDTO employee = validateAndGetEmployee(request);

        // 직원이 참여중인 프로젝트 목록 조회
        List<ProjectDTO> projects = projectService.getProjectsByEmployee(employee.getEmpNum());

        // 직원의 업무 목록 조회
        List<TaskDTO> assignedTasks = taskService.getTasksByAssignee(employee.getEmpNum());

        // 직원의 To-Do 목록 조회
        List<TodoDTO> todoList = todoService.getTodoListByEmployee(employee.getEmpNum());

        // Todo 아이템의 남은 일수와 지연 여부 계산
        todoList = todoList.stream()
                .map(todo -> {
                    if (todo.getDueDate() != null) {
                        LocalDate now = LocalDate.now();
                        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(now, todo.getDueDate());
                        todo.setRemainingDays((int) daysLeft);
                        todo.setOverdue(daysLeft < 0 && !todo.isCompleted());
                    }
                    return todo;
                })
                .collect(Collectors.toList());

        // 업무 통계 정보 조회
        TaskSummaryDTO taskSummary = taskStatisticsService.getEmployeeTaskSummary(employee.getEmpNum());

        model.addAttribute("employee", employee);
        model.addAttribute("projects", projects);
        model.addAttribute("assignedTasks", assignedTasks);
        model.addAttribute("todoList", todoList);
        model.addAttribute("taskSummary", taskSummary);

        return "projects/work-management";
    }

    /**
     * 업무 등록 페이지
     */
    @GetMapping("/register")
    public String taskRegister(HttpServletRequest request, Model model) {
        EmployeesDTO employee = validateAndGetEmployee(request);

        // 직원이 접근 가능한 프로젝트 목록 조회
        List<ProjectDTO> accessibleProjects = projectService.getAccessibleProjects(employee.getEmpNum());

        // 부서 목록 조회
        List<DepartmentsDTO> departments = departmentsService.getAllDepartments();

        // 직원 목록 조회 (업무 담당자 지정용)
        List<EmployeesDTO> employees = employeesService.getAllEmployees();

        model.addAttribute("employee", employee);
        model.addAttribute("accessibleProjects", accessibleProjects);
        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);

        return "projects/task-register";
    }

    /**
     * 대시보드 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        EmployeesDTO employee = validateAndGetEmployee(request);

        // 직원이 참여중인 프로젝트 목록 (최대 5개)
        List<ProjectDTO> recentProjects = projectService.getProjectsByEmployee(employee.getEmpNum());
        if (recentProjects.size() > 5) {
            recentProjects = recentProjects.subList(0, 5);
        }

        // 직원의 담당 업무 목록 (최대 10개)
        List<TaskDTO> assignedTasks = taskService.getTasksByAssignee(employee.getEmpNum());
        if (assignedTasks.size() > 10) {
            assignedTasks = assignedTasks.subList(0, 10);
        }

        // 메타데이터 처리
        assignedTasks = assignedTasks.stream()
                .map(task -> {
                    if (task.getDueDate() != null) {
                        LocalDate now = LocalDate.now();
                        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(now, task.getDueDate());
                        task.setRemainingDays((int) daysLeft);
                        task.setOverdue(daysLeft < 0 && !"완료".equals(task.getStatus()));
                    }
                    return task;
                })
                .collect(Collectors.toList());

        // 오늘 할 일 목록
        List<TodoDTO> todayTodos = todoService.getTodosByDate(employee.getEmpNum(), LocalDate.now());

        // 오늘 및 다가오는 일정 (7일 이내)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        List<ScheduleDTO> upcomingSchedules = scheduleService.getSchedulesByDateRange(now, sevenDaysLater);

        // 업무 통계 정보
        TaskSummaryDTO taskSummary = taskStatisticsService.getEmployeeTaskSummary(employee.getEmpNum());

        model.addAttribute("employee", employee);
        model.addAttribute("recentProjects", recentProjects);
        model.addAttribute("assignedTasks", assignedTasks);
        model.addAttribute("todayTodos", todayTodos);
        model.addAttribute("upcomingSchedules", upcomingSchedules);
        model.addAttribute("taskSummary", taskSummary);

        return "projects/dashboard";
    }

    /**
     * 프로젝트 상세 정보 조회 (AJAX용)
     */
    @GetMapping("/project/{projectId}")
    @ResponseBody
    public ResponseEntity<ProjectDTO> getProjectDetail(@PathVariable Integer projectId) {
        ProjectDTO project = projectService.getProjectById(projectId);
        if (project == null) {
            throw new com.example.projectdemo.exception.ResourceNotFoundException("프로젝트를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(project);
    }

    /**
     * 프로젝트 완료 여부 확인
     */
    @GetMapping("/project/{projectId}/completed")
    @ResponseBody
    public ResponseEntity<Boolean> isProjectCompleted(@PathVariable Integer projectId) {
        boolean isCompleted = projectService.isProjectCompleted(projectId);
        return ResponseEntity.ok(isCompleted);
    }
}