package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/delete")
    public ResponseEntity deleteMembership(@RequestParam int roomid, HttpServletRequest request) {
        int userId = (int) request.getAttribute("id");
        System.out.println(roomid + " 삭제할 방 아이디");
        String result = membershipService.deleteMembership(roomid,userId);
        return ResponseEntity.ok(result);
    }

}
