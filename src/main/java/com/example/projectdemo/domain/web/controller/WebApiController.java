package com.example.projectdemo.domain.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebApiController {
    //하드 리프레시 문제 해결용
    @GetMapping(value = {"/admin", "/admin/**",  "/api/admin/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
