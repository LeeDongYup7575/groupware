package com.example.projectdemo.mongodb.repository;

import com.example.projectdemo.domain.chat.dto.UnreadMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UnreadMessageRepository extends MongoRepository<UnreadMessage, String> {
    List<UnreadMessage> findByUserIdAndChatroomId(int userId, int chatroomId);

    List<UnreadMessage> findByUserId(int userId);

    void deleteByUserIdAndChatroomId(int userId, int chatroomId);

    long countByUserIdAndChatroomId(int chatroomId,int userId);
}
