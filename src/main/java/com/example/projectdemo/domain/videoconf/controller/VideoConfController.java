package com.example.projectdemo.domain.videoconf.controller;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class VideoConfController {

    private final EmployeesService employeesService;

    // 화상회의 로비 페이지 요청 처리
    @GetMapping("/videoconf-lobby")
    public String getVideoConfLobbyPage(HttpServletRequest request, Model model) {
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
        return "videochat/videoconf-lobby";
    }

    // 화상회의실 페이지 요청 처리
    @GetMapping("/videoconf-room")
    public String getVideoConfRoomPage(
            @RequestParam(required = false) String roomId,
            HttpServletRequest request,
            Model model) {

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

        // roomId가 없으면 로비로 리다이렉트
        if (roomId == null || roomId.isEmpty()) {
            return "redirect:/videoconf-lobby";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("roomId", roomId);
        return "videochat/videoconf-room";
    }

    // 기존 videochat 엔드포인트 (하위 호환성 유지)
    @GetMapping("/videochat")
    public String redirectToVideoconfLobby() {
        return "redirect:/videoconf-lobby";
    }
}