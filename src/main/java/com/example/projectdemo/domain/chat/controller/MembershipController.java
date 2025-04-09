package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.dto.ChatUserDTO;
import com.example.projectdemo.domain.chat.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // ✅ 이 클래스는 REST API 전용 컨트롤러
@RequestMapping("/membership")  // ✅ 기본 URL을 "/membership"으로 설정
public class MembershipController {

    @Autowired
    private MembershipService membershipService;  // ✅ 멤버십 관련 비즈니스 로직을 처리하는 서비스 주입

    @PostMapping("/delete")  // ✅ 채팅방 나가기 또는 방 삭제 요청 처리 (POST /membership/delete)
    public ResponseEntity<String> deleteMembership(@RequestParam int roomid, HttpServletRequest request) {
        int userId = (int) request.getAttribute("id");  // ✅ 요청한 사용자 ID 가져오기
        String result = membershipService.deleteMembership(roomid, userId);  // ✅ 방 나가기 or 방 삭제 처리
        return ResponseEntity.ok(result);  // ✅ 처리 결과(success / fail / exit) 반환
    }

    @GetMapping("/getuserlist")  // ✅ 특정 채팅방의 참여자 목록 조회 (GET /membership/getuserlist?roomId=xxx)
    public ResponseEntity<List<ChatUserDTO>> getUserList(HttpServletRequest request, @RequestParam int roomId) {
        int userId = (int) request.getAttribute("id");  // ✅ 요청한 사용자 ID 가져오기

        if (userId == 0 || roomId == 0) {  // ❗ 사용자 ID나 방 ID가 0이면 잘못된 요청
            return ResponseEntity.badRequest().build();  // ✅ 400 Bad Request 반환
        }

        return ResponseEntity.ok(membershipService.getUserList(userId, roomId));  // ✅ 정상적으로 참여자 목록 반환
    }
}
