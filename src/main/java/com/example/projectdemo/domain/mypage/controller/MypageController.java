package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {
    private final JwtTokenUtil jwtTokenUtil;
    private final EmployeesService employeesService;

    @Autowired
    public MypageController(JwtTokenUtil jwtTokenUtil, EmployeesService employeesService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeesService = employeesService;
    }

    @GetMapping
    public String mypage(Model model, HttpServletRequest request) {
        String empNum = (String)request.getAttribute("empNum");

        EmployeesDTO employee = employeesService.findByEmpNum(empNum);
        model.addAttribute("employee", employee);
        return "mypage/mypage";
    }

}
