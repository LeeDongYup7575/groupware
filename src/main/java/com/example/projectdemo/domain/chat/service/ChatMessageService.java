package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.mongodb.repository.ChatMessageRepository;
import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service  // ✅ Service 레이어 - 채팅 메시지 관련 로직 담당
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;  // ✅ MongoDB 저장소 주입

    // ✅ 채팅 메시지 저장
    public void saveMessage(ChatMessageDTO message) {
        System.out.println(message.getContent());  // 디버깅용 콘솔 출력
        chatMessageRepository.save(message);       // MongoDB에 메시지 저장
    }

    // ✅ 특정 채팅방의 모든 메시지(오래된 순서) 가져오기
    public List<ChatMessageDTO> getMessagesByRoom(int chatroomId) {
        return chatMessageRepository.findByChatroomIdOrderBySentAtAsc(chatroomId);
    }

    // ✅ 특정 채팅방의 가장 마지막(최신) 메시지 가져오기
    public ChatMessageDTO getLastMessageByRoom(int chatroomId) {
        return chatMessageRepository.findFirstByChatroomIdOrderBySentAtDesc(chatroomId);
    }

    // ✅ 특정 채팅방의 모든 메시지 삭제
    public void deleteByChatroomId(int chatroomId) {
        chatMessageRepository.deleteByChatroomId(chatroomId);
    }
}
