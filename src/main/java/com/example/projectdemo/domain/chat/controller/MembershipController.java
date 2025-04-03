package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.dto.ChatUserDTO;
import com.example.projectdemo.domain.chat.dto.MemberShipDTO;
import com.example.projectdemo.domain.chat.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/delete")
    public ResponseEntity deleteMembership(@RequestParam int roomid, HttpServletRequest request) {
        int userId = (int) request.getAttribute("id");
        System.out.println(roomid + " 삭제할 방 아이디");
        String result = membershipService.deleteMembership(roomid, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getuserlist")
    public ResponseEntity<List<ChatUserDTO>> getUserList(HttpServletRequest request, @RequestParam int roomId) {
        System.out.println(roomId + " : 유저리스트 받으려고 넘어온 채팅방아이디");
        int userId = (int) request.getAttribute("id");
        if(userId==0||roomId==0){
            return null;
        }
        return ResponseEntity.ok(membershipService.getUserList(userId, roomId));
    }

}
