package com.example.projectdemo.domain.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * 홈 페이지 (인트로)
     */
//    @GetMapping("/")
//    public String home() {
//        return "intro";
//    }
    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    /**
     * 로그인 페이지 (auth 경로)
     */
    @GetMapping("/auth/login")
    public String authLogin() {
        return "auth/login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/auth/signup")
    public String signup() {
        return "auth/signup";
    }
    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/auth/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }
    /**
     * 비밀번호 변경 페이지
     */
    @GetMapping("/auth/change-password")
    public String changePassword() {
        return "auth/change-password";
    }
}