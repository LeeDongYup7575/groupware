package com.example.projectdemo.domain.mypage.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    @GetMapping
    public String mypage() {return "mypage/mypage";}

}
