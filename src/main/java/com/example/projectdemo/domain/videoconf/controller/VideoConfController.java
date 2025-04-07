package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class VideoConfController {

    private final EmployeesService employeesService;

    // 화상 채팅 페이지 요청 처리
    @GetMapping("/videochat")
    public String getVideoChatPage(HttpServletRequest request, Model model) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String)request.getAttribute("empNum");

        if (empNum == null) {
            return "redirect:/auth/login";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeesService.findByEmpNum(empNum);

        if (employee == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("employee", employee);
        return "videochat/videochat";
    }
}