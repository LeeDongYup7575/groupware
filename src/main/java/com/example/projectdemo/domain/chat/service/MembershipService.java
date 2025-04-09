package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatUserDTO;
import com.example.projectdemo.domain.chat.dto.MemberShipDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // ✅ 서비스 레이어 - 채팅방 멤버십(참여자) 관련 로직
public class MembershipService {

    @Autowired
    private MembershipDAO membershipDAO; // ✅ DB 접근 (멤버십 테이블)

    @Autowired
    private EmployeesMapper employeesMapper; // ✅ DB 접근 (직원 정보 테이블)

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // ✅ WebSocket 알림 발송용

    @Autowired
    private ChatMessageService chatMessageService; // ✅ MongoDB 채팅 메시지 관리 서비스

    @Autowired
    private ChatRoomService chatRoomService; // ✅ 채팅방 삭제 관리 서비스

    // ✅ 채팅방 나가기 또는 채팅방 삭제 로직
    @Transactional
    public String deleteMembership(int roomid, int userid) {
        Map<String, Object> params = new HashMap<>();
        params.put("roomid", roomid); // 방 ID 설정
        params.put("userid", userid); // 사용자 ID 설정

        boolean isAdmin = membershipDAO.isAdmin(params); // ✅ 현재 유저가 일반 참여자인지, 관리자인지 판단

        if (!isAdmin) { // 👉 일반 멤버인 경우: 방 삭제
            int rs = membershipDAO.deleteChatRoom(roomid); // 방 통째로 삭제
            if (rs > 0) { // 삭제 성공 시
                Map<String, Object> payload = new HashMap<>();
                payload.put("roomid", roomid);
                messagingTemplate.convertAndSend("/topic/roomDeleted", payload); // WebSocket으로 방 삭제 알림
                chatMessageService.deleteByChatroomId(roomid); // MongoDB 메시지 삭제
                chatRoomService.deleteByChatroomId(roomid); // 채팅방 DB 삭제
                return "success"; // 성공 응답
            } else {
                return "fail"; // 삭제 실패 응답
            }
        } else { // 👉 관리자(본인이 방 생성자)인 경우: 그냥 "방 나가기"
            membershipDAO.deleteById(params); // 자기 자신만 멤버 리스트에서 제거
            return "ExitChatroom"; // 방은 유지
        }
    }

    // ✅ 채팅방 참여자 리스트 조회
    public List<ChatUserDTO> getUserList(int userid, int roomid) {
        List<MemberShipDTO> list = membershipDAO.getUserList(roomid); // 해당 방의 모든 멤버 조회
        if (list.isEmpty()) {
            return List.of(); // 참여자가 없으면 빈 리스트 리턴
        }

        List<ChatUserDTO> memberList = new ArrayList<>();
        for (MemberShipDTO dto : list) { // 멤버십 정보를 순회
            int id = dto.getEmpId();
            EmployeesDTO emp = employeesMapper.findById(id); // 직원 상세 정보 조회
            if (emp != null) { // 직원이 존재하면
                memberList.add(new ChatUserDTO(id, emp.getName())); // 사용자 정보 리스트에 추가
            }
        }
        return memberList; // 최종 참여자 리스트 반환
    }
}
