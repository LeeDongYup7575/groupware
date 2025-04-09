package com.example.projectdemo.domain.chat.dao;

import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository  // ✅ DAO(Data Access Object) - 데이터베이스 접근 담당 클래스
public class ChatRoomDAO {

    @Autowired
    private SqlSession mybatis;  // ✅ MyBatis 세션 주입 (쿼리 수행용)

    // ✅ 사용자가 참여 중인 채팅방 목록 조회
    public List<ChatRoomDTO> getChatRoom(List<Integer> getChatRoomIds) {
        return mybatis.selectList("chatRoom.getChatRoom", getChatRoomIds);
    }

    // ✅ 새로운 채팅방 생성
    public int createChatRoom(ChatRoomDTO room) {
        mybatis.insert("chatRoom.createChatRoom", room);  // ✅ 채팅방 정보 DB에 저장
        return room.getId();  // ✅ 생성된 방의 ID 반환 (MyBatis가 insert 후 자동 세팅)
    }

    // ✅ 채팅방 검색 (이름 검색)
    public List<ChatRoomDTO> searchList(Map<String, Object> params) {
        return mybatis.selectList("chatRoom.searchList", params);
    }

    // ✅ 채팅방 ID로 채팅방 삭제
    public void deleteByChatroomId(int roomId) {
        mybatis.delete("chatRoom.deleteByChatroomId", roomId);
    }
}
