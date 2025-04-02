package com.example.projectdemo.mongodb.repository;

import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDTO,String> {
    List<ChatMessageDTO> findByChatroomIdOrderBySentAtAsc(int chatroomId);
}
