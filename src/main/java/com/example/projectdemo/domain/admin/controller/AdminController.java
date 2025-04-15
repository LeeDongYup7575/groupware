//package com.example.projectdemo.domain.admin.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.view.RedirectView;
//
//@Controller
//@RequestMapping("/admin")
//public class AdminController {
//
//    @Value("${admin.frontend.url:http://localhost:3000}") //개발환경이니까 일단 이렇게 해놓고 나중에 배포 예정
//    private String adminFrontendUrl;
//
//    @GetMapping
//    public RedirectView redirectToAdminPage() {
//        return new RedirectView(adminFrontendUrl);
//    }
//}
//
