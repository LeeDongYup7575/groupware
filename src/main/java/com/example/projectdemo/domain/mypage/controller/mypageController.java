package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/mypage")
public class mypageController {
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesService employeesService;

//    String empNum = (String)request.getAttribute("empNum");

    @GetMapping
    public String mypage(Model model) {
        // 사번 수정 필요
        EmployeesDTO employee = employeesService.findByEmpNum("00004");
        model.addAttribute("employee", employee);
        return "mypage/mypage";
    }

}
