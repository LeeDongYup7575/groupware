package com.example.projectdemo.domain.org.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/org")
public class OrgController {

    @GetMapping
    public String org(Model model) {
        return "org/org";
    }

}
