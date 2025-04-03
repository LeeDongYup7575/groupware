package com.example.projectdemo.domain.chat.dao;

import com.example.projectdemo.domain.chat.dto.MemberShipDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MembershipDAO {

    @Autowired
    private SqlSession mybatis;

    public List<Integer> getChatroomIds(int id) {
        return mybatis.selectList("memberShip.getChatRoomIds", id);
    }

    public int insertMember(int roomId, int empId, int role) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("roomId", roomId);
        param.put("empId", empId);
        param.put("role", role);
        return mybatis.insert("memberShip.insertMember", param);
    }


    public int deleteChatRoom(int roomid) {
        return mybatis.delete("memberShip.deleteChatroom", roomid);
    }
    public int deleteById(Map<String, Object> params) {
        return mybatis.delete("memberShip.deleteById", params);
    }
    public boolean isAdmin(Map<String, Object> params) {
        return mybatis.selectOne("memberShip.isAdmin", params);
    }
    public List<MemberShipDTO> getUserList(int roomid) {
        return mybatis.selectList("memberShip.getUserList", roomid);
    }
}
