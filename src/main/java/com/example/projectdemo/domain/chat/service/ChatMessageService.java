package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.mongodb.repository.ChatMessageRepository;
import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    public void saveMessage(ChatMessageDTO message) {
        
        System.out.println(message.getContent());
        // mongodb에 저장하기
        chatMessageRepository.save(message);
    }

    public List<ChatMessageDTO> getMessagesByRoom(int chatroomId) {
        return chatMessageRepository.findByChatroomIdOrderBySentAtAsc(chatroomId);
    }
}
