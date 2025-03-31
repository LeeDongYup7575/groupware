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

@RestController
@RequestMapping("/chatroom")
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getChatRoom(HttpServletRequest request) {
        System.out.println(request.getAttribute(""));
        // 개발단계에서 그냥 id값 하드코딩
        int id = 3;
        // int id = (int)request.getAttribute("id");
        return ResponseEntity.ok(chatRoomService.getChatRoom(id));
    }
    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        chatRoomService.createChatRoom(chatRoomDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getaddlist")
    public ResponseEntity<List<EmployeesDTO>> getAddList(HttpServletRequest request){

        return ResponseEntity.ok((List<EmployeesDTO>) chatRoomService.getAddList());
    }
//    @PostMapping("/addroom")
//    public ResponseEntity addRoom(ChatRoomRequestDTO request ) {
//        return chatRoomService.createChatRoom(request);
//    }

}
