package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import com.example.projectdemo.domain.chat.service.ChatMessageService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatStompController {

    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private EmployeesMapper employeesMapper;


    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDTO handleMessage(@DestinationVariable int roomId, ChatMessageDTO message) {
        System.out.println(roomId + " : " + message);

        // message에서 나온 id로 이름을 조회
        EmployeesDTO sender = employeesMapper.findById(message.getSenderId());
        // 이름 저장
        message.setSenderName(sender.getName());
        // 시간 저장
        message.setSentAt(new Date());
        // 채팅방 id저장
        message.setChatroomId(roomId);
        // mongodb에 저장
        chatMessageService.saveMessage(message);
        // 프론트 단으로 메시지 전송
        return message;
    }

    @GetMapping("/message/{roomId}")
    public List<ChatMessageDTO> getMessagesByRoom(@PathVariable int roomId) {
        List<ChatMessageDTO>list = chatMessageService.getMessagesByRoom(roomId);
        if(!list.isEmpty()){
            return list;
        } else {
            return null;
        }

    }
}
