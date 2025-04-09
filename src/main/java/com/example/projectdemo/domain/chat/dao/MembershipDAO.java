package com.example.projectdemo.domain.chat.dao;

import com.example.projectdemo.domain.chat.dto.MemberShipDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository  // ✅ DAO(Data Access Object) - DB 접근 담당 클래스
public class MembershipDAO {

    @Autowired
    private SqlSession mybatis;  // ✅ MyBatis 세션 주입 (쿼리 실행 담당)

    // ✅ 특정 사용자가 참여 중인 모든 채팅방 ID 목록 조회
    public List<Integer> getChatroomIds(int id) {
        return mybatis.selectList("memberShip.getChatRoomIds", id);
    }

    // ✅ 채팅방에 새 멤버 추가 (방 ID, 사원 ID, 역할)
    public int insertMember(int roomId, int empId, int role) {
        Map<String, Object> param = new HashMap<>();  // ✅ 파라미터 묶기
        param.put("roomId", roomId);
        param.put("empId", empId);
        param.put("role", role);
        return mybatis.insert("memberShip.insertMember", param);  // ✅ insert 쿼리 실행
    }

    // ✅ 채팅방 전체 삭제 (방 아이디 기준)
    public int deleteChatRoom(int roomid) {
        return mybatis.delete("memberShip.deleteChatroom", roomid);
    }

    // ✅ 특정 사용자를 채팅방에서 나가게 하기 (방 ID + 유저 ID)
    public int deleteById(Map<String, Object> params) {
        return mybatis.delete("memberShip.deleteById", params);
    }

    // ✅ 현재 사용자가 채팅방에서 관리자인지 여부 조회
    public boolean isAdmin(Map<String, Object> params) {
        return mybatis.selectOne("memberShip.isAdmin", params);
    }

    // ✅ 특정 채팅방의 모든 참여자 목록 조회
    public List<MemberShipDTO> getUserList(int roomid) {
        return mybatis.selectList("memberShip.getUserList", roomid);
    }

    public List<Integer> getMemberIdsByRoomId(int roomid) {
        return mybatis.selectList("memberShip.getMemberIdsByRoomId", roomid);
    }
}
