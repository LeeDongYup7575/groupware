package com.example.projectdemo.domain.chat.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MembershipDAO {

    @Autowired
    private SqlSession mybatis;

    public List<Integer> getChatroomIds(int id) {
        return mybatis.selectList("memberShip.getChatRoomIds", id);
    }
}
