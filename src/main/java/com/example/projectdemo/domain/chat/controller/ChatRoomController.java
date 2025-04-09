package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import com.example.projectdemo.domain.chat.dto.ChatRoomRequestDTO;
import com.example.projectdemo.domain.chat.service.ChatRoomService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // ✅ REST API 컨트롤러임을 명시
@RequestMapping("/chatroom")  // ✅ 모든 요청의 기본 경로를 "/chatroom"으로 설정
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;  // ✅ 채팅방 관련 비즈니스 로직을 처리할 서비스 주입

    @GetMapping  // ✅ 채팅방 목록 조회 요청 (GET /chatroom)
    public ResponseEntity<List<ChatRoomDTO>> getChatRoom(HttpServletRequest request) {
        int id = (int) request.getAttribute("id");  // ✅ 요청에 저장된 사용자 ID 꺼냄 (JWT 필터에서 넣어줬을 것)
        return ResponseEntity.ok(chatRoomService.getChatRoom(id));  // ✅ 사용자가 속한 채팅방 리스트 반환
    }

    @GetMapping("/getaddlist")  // ✅ 채팅방 생성 시 초대 가능한 사원 목록 조회 (GET /chatroom/getaddlist)
    public ResponseEntity<List<EmployeesDTO>> getAddList(HttpServletRequest request) {
        int id = (int) request.getAttribute("id");  // ✅ 요청자(자기 자신) ID 꺼내기
        return ResponseEntity.ok(chatRoomService.getAddList(id));  // ✅ 자기 자신 제외한 직원 리스트 반환
    }

    @PostMapping("/addroom")  // ✅ 새로운 채팅방 생성 요청 (POST /chatroom/addroom)
    public ResponseEntity<ChatRoomDTO> addRoom(@RequestBody ChatRoomRequestDTO request, HttpServletRequest resq) {
        int id = (int) resq.getAttribute("id");  // ✅ 요청한 사용자 ID 꺼내기
        return ResponseEntity.ok(chatRoomService.addRoom(request, id));  // ✅ 채팅방 생성 후 생성된 방 정보 반환
    }

    @GetMapping("/search")  // ✅ 채팅방 이름으로 검색 요청 (GET /chatroom/search?target=검색어)
    public ResponseEntity<List<ChatRoomDTO>> search(HttpServletRequest request, @RequestParam String target) {
        int id = (int) request.getAttribute("id");  // ✅ 요청자 ID 꺼내기
        return ResponseEntity.ok(chatRoomService.searchList(target, id));  // ✅ 검색어에 맞는 채팅방 리스트 반환
    }
}
