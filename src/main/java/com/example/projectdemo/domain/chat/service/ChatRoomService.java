package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.ChatRoomDAO;
import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
import com.example.projectdemo.domain.chat.dto.ChatRoomRequestDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomDAO dao;
    @Autowired
    private MembershipDAO mDao;
    @Autowired
    private EmployeesMapper employeesMapper;

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
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getId() == id) {
                list.remove(i);
            }
        }
        return list;
    }

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

        return new ChatRoomDTO();
    }
}
