package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.service.UnreadMessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/unread")
public class UnreadMessageController {

    @Autowired
    private UnreadMessageService unreadMessageService;

    @PostMapping("/clear")
    public void clearUnreadMessages(@RequestBody Map<String,Integer> resq, HttpServletRequest request) {
        System.out.println("받은 데이터: " + resq);   // 🔥 추가
        int userId = (int) request.getAttribute("id");
        int chatroomId = resq.get("roomId");
        unreadMessageService.deleteUnreadMessage(chatroomId, userId);
    }
    @GetMapping("/count")
    public Map<Integer,Long> getUnreadCounts(HttpServletRequest request){
        int userId = (int) request.getAttribute("id");
        return unreadMessageService.getUnreadMessagesCountByUser(userId);
    }
}
