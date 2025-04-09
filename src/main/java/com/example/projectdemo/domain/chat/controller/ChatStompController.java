package com.example.projectdemo.domain.chat.controller;

import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import com.example.projectdemo.domain.chat.service.ChatMessageService;
import com.example.projectdemo.domain.chat.service.UnreadMessageService;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Date;
import java.util.List;

@RestController  // ✅ 이 클래스는 REST API와 WebSocket 메시지를 둘 다 처리
@RequestMapping("/chat")  // ✅ 기본 URL을 "/chat"으로 설정
public class ChatStompController {

    @Autowired
    private ChatMessageService chatMessageService;  // ✅ 채팅 메시지 저장/조회 기능을 담당하는 서비스 주입

    @Autowired
    private EmployeesMapper employeesMapper;  // ✅ 사용자 이름을 조회하기 위해 EmployeesMapper 주입

    @Autowired
    private UnreadMessageService unreadMessageService;

    @Autowired
    private MembershipDAO membershipDAO;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{roomId}")  // ✅ 클라이언트가 "/app/chat/{roomId}"로 메시지를 보낼 때 실행
    @SendTo("/topic/chat/{roomId}")  // ✅ 해당 채팅방의 구독자들에게 메시지를 브로드캐스팅
    public ChatMessageDTO handleMessage(@DestinationVariable int roomId, ChatMessageDTO message) {
        EmployeesDTO sender = employeesMapper.findById(message.getSenderId());  // ✅ senderId로 사용자 정보 조회

        if (sender != null) {
            message.setSenderName(sender.getName());  // ✅ 사용자가 존재하면 이름 세팅
        } else {
            message.setSenderName("알 수 없는 사용자");  // ✅ 사용자가 없으면 기본값 세팅 (예외 방어)
        }

        message.setSentAt(new Date());  // ✅ 메시지 보낸 시간 세팅
        message.setChatroomId(roomId);  // ✅ 채팅방 ID 세팅


        try {
            chatMessageService.saveMessage(message);  // ✅ MongoDB에 메시지 저장 시도
        } catch (Exception e) {
            System.out.println("❗ MongoDB 저장 실패: " + e.getMessage());  // ✅ 저장 실패해도 서버 다운 방지
        }
        List<Integer> memberIds = membershipDAO.getMemberIdsByRoomId(roomId);
        try {
            for (Integer memberId : memberIds) {
                if (memberId != message.getSenderId()) {
                    unreadMessageService.saveUnreadMessage(memberId, roomId, message.getId());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;  // ✅ 최종 메시지를 브로드캐스팅
    }

    @GetMapping("/message/{roomId}")  // ✅ 채팅방의 과거 메시지 목록 조회 (GET /chat/message/{roomId})
    public List<ChatMessageDTO> getMessagesByRoom(@PathVariable int roomId) {
        List<ChatMessageDTO> list = chatMessageService.getMessagesByRoom(roomId);  // ✅ 채팅방 ID로 메시지 조회

        if (!list.isEmpty()) {
            return list;  // ✅ 메시지가 있으면 리스트 반환
        } else {
            return null;  // ❗ 메시지가 없으면 null 반환 (추후 빈 리스트 반환하는 게 더 안전)
        }
    }
}
