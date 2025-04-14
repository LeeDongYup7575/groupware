package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.DepartmentsDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.DepartmentsService;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TodoDTO;
import com.example.projectdemo.domain.projects.service.ProjectService;
import com.example.projectdemo.domain.projects.service.TaskService;
import com.example.projectdemo.domain.projects.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/workmanagement")
public class WorkManagementController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private DepartmentsService departmentsService;

    /**
     * 업무 관리 메인 페이지 (업무 탭)
     */
    @GetMapping
    public String workManagementMain(HttpServletRequest request, Model model) {
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

        // 직원이 참여중인 프로젝트 목록 조회
        List<ProjectDTO> projects = projectService.getProjectsByEmployee(empNum);

        // 직원의 업무 목록 조회
        List<TaskDTO> assignedTasks = taskService.getTasksByAssignee(empNum);

        // 직원의 To-Do 목록 조회
        List<TodoDTO> todoList = todoService.getTodoListByEmployee(empNum);

        model.addAttribute("employee", employee);
        model.addAttribute("projects", projects);
        model.addAttribute("assignedTasks", assignedTasks);
        model.addAttribute("todoList", todoList);

        return "projects/work-management";
    }

    // WorkManagementController.java 수정
    @GetMapping("/register")
    public String taskRegister(HttpServletRequest request, Model model) {
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

        // 부서 목록 조회
        List<DepartmentsDTO> departments = departmentsService.getAllDepartments();

        // 직원 목록 조회 (업무 담당자 지정용)
        List<EmployeesDTO> employees = employeesService.getAllEmployees();

        model.addAttribute("employee", employee);
        model.addAttribute("accessibleProjects", accessibleProjects);
        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);

        return "projects/task-register"; // 기존 템플릿 재사용
    }

//    /**
//     * 일정 탭
//     */
//    @GetMapping("/schedule")
//    public String projectSchedule(HttpServletRequest request,
//                                  @RequestParam(required = false) Integer projectId,
//                                  Model model) {
//        // JWT 필터에서 설정한 사원번호 추출
//        String empNum = (String) request.getAttribute("empNum");
//
//        if (empNum == null) {
//            return "redirect:/auth/login";
//        }
//
//        // 사원번호로 직원 정보 조회
//        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
//        if (employee == null) {
//            return "redirect:/auth/login";
//        }
//
//        // 직원이 접근 가능한 프로젝트 목록 조회
//        List<ProjectDTO> accessibleProjects = projectService.getAccessibleProjects(empNum);
//
//        model.addAttribute("employee", employee);
//        model.addAttribute("accessibleProjects", accessibleProjects);
//        model.addAttribute("selectedProjectId", projectId);
//
//        return "projects/project-schedule";
//    }

    /**
     * 프로젝트 상세 정보 조회 (AJAX용)
     */
    @GetMapping("/project/{projectId}")
    @ResponseBody
    public ResponseEntity<ProjectDTO> getProjectDetail(@PathVariable Integer projectId) {
        ProjectDTO project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }
}