package com.example.projectdemo.domain.contact.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/contact")
public class ContactController {

    @GetMapping
    public String contact(Model model) {

        return "contact/contact";
    }
}
