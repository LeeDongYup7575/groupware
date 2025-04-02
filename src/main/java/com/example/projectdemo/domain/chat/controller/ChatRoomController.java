package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import com.example.projectdemo.domain.chat.dto.ChatRoomRequestDTO;
import com.example.projectdemo.domain.chat.service.ChatRoomService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
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
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeesService employeeService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getChatRoom(HttpServletRequest request) {
        int id = (int) request.getAttribute("id");
        return ResponseEntity.ok(chatRoomService.getChatRoom(id));
    }


    @GetMapping("/getaddlist")
    public ResponseEntity<List<EmployeesDTO>> getAddList(HttpServletRequest request) {
        int id=(int) request.getAttribute("id");
        return ResponseEntity.ok(chatRoomService.getAddList(id));
    }
    @PostMapping("/addroom")
    public ResponseEntity<ChatRoomDTO> addRoom(@RequestBody ChatRoomRequestDTO request, HttpServletRequest resq) {
        int id=(int) resq.getAttribute("id");
        return ResponseEntity.ok(chatRoomService.addRoom(request, id));
    }

}
