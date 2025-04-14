package com.example.projectdemo.domain.projects.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.projects.dto.TodoDTO;
import com.example.projectdemo.domain.projects.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/workmanagement/todo")
public class TodoController {

    @Autowired
    private EmployeesService employeesService;

    @Autowired
    private TodoService todoService;

    /**
     * 할 일 관리 페이지
     */
    @GetMapping
    public String todoMain(HttpServletRequest request,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           @RequestParam(required = false) String priority,
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

        // 할 일 목록 조회
        List<TodoDTO> todoList;

        if (date != null) {
            // 특정 날짜의 할 일만 조회
            todoList = todoService.getTodosByDate(empNum, date);
            model.addAttribute("selectedDate", date);
        } else if (priority != null && !priority.isEmpty()) {
            // 특정 우선순위의 할 일만 조회
            todoList = todoService.getTodosByPriority(empNum, priority);
            model.addAttribute("selectedPriority", priority);
        } else {
            // 모든 할 일 조회
            todoList = todoService.getTodoListByEmployee(empNum);
        }

        // 할 일 완료 여부에 따른 분류
        List<TodoDTO> completedTodos = todoList.stream()
                .filter(TodoDTO::isCompleted)
                .toList();

        List<TodoDTO> incompleteTodos = todoList.stream()
                .filter(todo -> !todo.isCompleted())
                .toList();

        model.addAttribute("employee", employee);
        model.addAttribute("todoList", todoList);
        model.addAttribute("completedTodos", completedTodos);
        model.addAttribute("incompleteTodos", incompleteTodos);
        model.addAttribute("currentDate", date != null ? date : LocalDate.now());

        return "projects/todo";
    }
}