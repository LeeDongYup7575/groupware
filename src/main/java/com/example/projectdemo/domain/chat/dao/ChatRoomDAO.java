package com.example.projectdemo.domain.chat.dao;

import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatRoomDAO {
    @Autowired
    private SqlSession mybatis;

    public List<ChatRoomDTO> getChatRoom(List<Integer> getChatRoomIds) {
        return mybatis.selectList("chatRoom.getChatRoom", getChatRoomIds);
    }

    public int createChatRoom(ChatRoomDTO room) {
        mybatis.insert("chatRoom.createChatRoom", room);
        return room.getId();
    }
}
