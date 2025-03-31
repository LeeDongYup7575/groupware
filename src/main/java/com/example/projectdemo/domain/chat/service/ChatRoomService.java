package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.ChatRoomDAO;
import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatRoomDTO;
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
        System.out.println(idList+"아이디 list");
        return dao.getChatRoom(idList);
    }

    public int createChatRoom(ChatRoomDTO chatRoomDTO) {
        return dao.createChatRoom(chatRoomDTO);
    }

    public List<EmployeesDTO> getAddList() {
        return employeesMapper.selectEmpAll();
    }
}
