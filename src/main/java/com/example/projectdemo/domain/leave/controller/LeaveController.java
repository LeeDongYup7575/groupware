package com.example.projectdemo.domain.leave.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.leave.service.LeavesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/leaves")
public class LeaveController {

//    @Autowired
//    private LeavesService leavesService;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesMapper employeeMapper;

    @Autowired
    private EmployeesService employeeService;

    @RequestMapping("/leavesForm")
    public String leavesForm(Model model, HttpServletRequest request) {

        int empId = (int)request.getAttribute("id");

        if (empId == 0) { //예외처리
            return "redirect:/auth/login";
        }

        EmployeesDTO employee = employeeMapper.findById(empId);
        if (employee == null) { //예외처리
            return "redirect:/auth/login";
        }

        return "/leave/leavesForm";
    }
}
