package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.ChatRoomDAO;
import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatMessageDTO;
import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import com.example.projectdemo.domain.chat.dto.ChatRoomRequestDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // ✅ Service 레이어 - 채팅방 비즈니스 로직 담당
public class ChatRoomService {

    @Autowired
    private ChatRoomDAO dao; // ✅ 채팅방 DB 접근

    @Autowired
    private MembershipDAO mDao; // ✅ 멤버십(참여자) DB 접근

    @Autowired
    private EmployeesMapper employeesMapper; // ✅ 직원 정보 조회용

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate; // ✅ STOMP 메시지 브로드캐스트용

    @Autowired
    private ChatMessageService chatMessageService; // ✅ 채팅 메시지 저장/조회 서비스

    // ✅ 사용자가 참여 중인 모든 채팅방 조회 + 최근 메시지 세팅
    public List<ChatRoomDTO> getChatRoom(int id) {
        List<Integer> idList = mDao.getChatroomIds(id); // 참여중인 채팅방 ID 리스트 조회
        if (idList == null || idList.isEmpty()) {       // 참여중인 방 없으면 빈 리스트 리턴
            return List.of();
        }
        List<ChatRoomDTO> chatRooms = dao.getChatRoom(idList); // 채팅방 상세 정보 조회

        // 각각 채팅방에 대해 최근 메시지 세팅
        for (ChatRoomDTO chatRoom : chatRooms) {
            ChatMessageDTO lastMessage = chatMessageService.getLastMessageByRoom(chatRoom.getId());
            if (lastMessage != null) {
                chatRoom.setLastMessage(lastMessage.getContent());
                chatRoom.setLastMessageTime(new Timestamp(lastMessage.getSentAt().getTime()));
            }
        }
        return chatRooms;
    }

    // ✅ 채팅방 생성 시 추가 가능한 직원 리스트 조회 (본인 제외)
    public List<EmployeesDTO> getAddList(int id) {
        List<EmployeesDTO> list = employeesMapper.selectEmpAll(); // 모든 직원 조회
        list.removeIf(emp -> emp.getId() == id);                  // 본인 제외
        return list;
    }

    // ✅ 채팅방 생성 로직 (채팅방 + 멤버 추가 + 알림 전송)
    @Transactional
    public ChatRoomDTO addRoom(ChatRoomRequestDTO request, int id) {
        List<Integer> memberList = request.getMembers();
        memberList.add(id); // 본인도 멤버로 추가
        String roomName = request.getName();

        // 채팅방 생성
        ChatRoomDTO room = new ChatRoomDTO();
        room.setName(roomName);
        int roomId = dao.createChatRoom(room); // DB에 채팅방 저장

        // 참여자 등록 (본인은 role 0, 다른 참여자는 role 1)
        for (Integer memberId : memberList) {
            int role = (memberId.equals(id)) ? 0 : 1;
            mDao.insertMember(roomId, memberId, role);
        }

        // 채팅방 생성 알림 발송
        ChatRoomDTO result = new ChatRoomDTO();
        result.setId(roomId);
        result.setName(roomName);
        simpMessagingTemplate.convertAndSend("/topic/chatroom/created", result);

        return result;
    }

    // ✅ 채팅방 이름 검색
    public List<ChatRoomDTO> searchList(String target, int id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("target", target);
        return dao.searchList(params); // 검색 조건으로 채팅방 리스트 조회
    }

    // ✅ 채팅방 삭제
    public void deleteByChatroomId(int roomId) {
        dao.deleteByChatroomId(roomId);
    }
}
