package com.example.projectdemo.domain.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebApiController {
    // /api/admin으로 들어가는 요청을 제외하고 모두 index.html로 반환
    @GetMapping("/{path:^(?!api\\/admin).*$}")
    public String forward() {
        return "forward:/index.html";
    }
}
