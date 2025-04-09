package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dto.UnreadMessage;
import com.example.projectdemo.mongodb.repository.UnreadMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnreadMessageService {

    @Autowired
    private UnreadMessageRepository unreadMessageRepository;

    public void saveUnreadMessage(int userId, int chatroomId, String messageId) {
        UnreadMessage unreadMessage = new UnreadMessage(messageId, userId, chatroomId);
        unreadMessageRepository.save(unreadMessage);
    }

    public void deleteUnreadMessage(int chatroomId, int userId) {
        unreadMessageRepository.deleteByUserIdAndChatroomId(userId, chatroomId);
    }

    public long getUnreadMessageCount(int userId, int chatroomId) {
        return unreadMessageRepository.countByUserIdAndChatroomId(userId, chatroomId);
    }


    public Map<Integer, Long> getUnreadMessagesCountByUser(int userId) {
        List<UnreadMessage> unreadMessages = unreadMessageRepository.findByUserId(userId);
        Map<Integer, Long> unreadMessagesCount = new HashMap<>();
        for (UnreadMessage unreadMessage : unreadMessages) {
//            long currentCount =
            unreadMessagesCount.put(unreadMessage.getChatroomId(), unreadMessagesCount.getOrDefault(unreadMessage.getChatroomId(), 0L) + 1);
        }
        return unreadMessagesCount;
    }
}
