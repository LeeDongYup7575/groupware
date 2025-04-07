package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.ChatRoomDAO;
import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import com.example.projectdemo.domain.chat.dto.ChatRoomRequestDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.build.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomDAO dao;
    @Autowired
    private MembershipDAO mDao;
    @Autowired
    private EmployeesMapper employeesMapper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<ChatRoomDTO> getChatRoom(int id) {
        // 사원 아이디 가져와야함
        List<Integer> idList = mDao.getChatroomIds(id);
        System.out.println(idList + "아이디 list");
        if (idList == null || idList.isEmpty()) {
            return List.of();
        }
        return dao.getChatRoom(idList);
    }

    public List<EmployeesDTO> getAddList(int id) {
        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                list.remove(i);
            }
        }
        return list;
    }

    @Transactional
    public ChatRoomDTO addRoom(ChatRoomRequestDTO request, int id) {
        List<Integer> memberList = request.getMembers();
        memberList.add(id);
        String roomName = request.getName();
        ChatRoomDTO room = new ChatRoomDTO();
        room.setName(roomName);
        System.out.println(memberList.size() + " : 몇명? " + roomName + " : 방이름");
        int roomId = dao.createChatRoom(room);
        for (Integer memberId : memberList) {
            int role = (memberId.equals(id)) ? 0 : 1;
            mDao.insertMember(roomId, memberId, role);
        }
        ChatRoomDTO result = new ChatRoomDTO();
        result.setId(roomId);
        result.setName(roomName);
        simpMessagingTemplate.convertAndSend("/topic/chatroom/created", result);
        return result;
    }

    public List<ChatRoomDTO> searchList(String target, int id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("target", target);

        List<ChatRoomDTO> list = dao.searchList(params);
        if (list.isEmpty()) {
            System.out.println("찾는 리스트가 존재하지 않습니다.");
            return List.of();
        }
        System.out.println("찾는 리스트 : " + list.get(0).getName());
        return list;
    }
}
