package com.example.projectdemo.mongodb.repository;

import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// ✅ MongoDB에 저장된 채팅 메시지를 관리하는 Repository 인터페이스
public interface ChatMessageRepository extends MongoRepository<ChatMessageDTO, String> {

    // ✅ 특정 채팅방의 모든 메시지를 전송 시간(sentAt) 오름차순으로 가져오기 (오래된 것부터 정렬)
    List<ChatMessageDTO> findByChatroomIdOrderBySentAtAsc(int chatroomId);

    // ✅ 특정 채팅방의 가장 최근 메시지 1개 가져오기 (전송 시간(sentAt) 내림차순 정렬 후 첫 번째)
    ChatMessageDTO findFirstByChatroomIdOrderBySentAtDesc(int chatroomId);

    // ✅ 특정 채팅방의 모든 메시지를 삭제하기
    void deleteByChatroomId(int chatroomId);
}
