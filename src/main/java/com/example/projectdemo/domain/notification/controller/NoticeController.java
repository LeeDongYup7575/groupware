package com.example.projectdemo.domain.notification.controller;

import com.example.projectdemo.domain.notification.crawler.NoticeCrawler;
import com.example.projectdemo.domain.notification.model.Notice;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class NoticeController {

    @GetMapping("/")
    public String home() {
        return "intro";
    }


}