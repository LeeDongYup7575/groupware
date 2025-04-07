package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatUserDTO;
import com.example.projectdemo.domain.chat.dto.MemberShipDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MembershipService {
    @Autowired
    private MembershipDAO membershipDAO;
    @Autowired
    private EmployeesMapper employeesMapper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public String deleteMembership(int roomid, int userid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roomid", roomid);
        params.put("userid", userid);
        System.out.println(userid + " : 삭제할려고 넘어온 아이디값");
        boolean isAdmin = membershipDAO.isAdmin(params);
        System.out.println(isAdmin + "관리자 확인여부에요");
        System.out.println(isAdmin ? "일반인" : "관리자");
        if (!isAdmin) {
            int rs = membershipDAO.deleteChatRoom(roomid);
            if (rs > 0) {
                System.out.println("방삭제 완료");
                Map<String,Object> payload = new HashMap<>();
                payload.put("roomid", roomid);
                messagingTemplate.convertAndSend("/topic/roomDeleted", payload);
                return "success";
            } else {
                System.out.println("방 삭제 실패");
                return "fail";
            }
        } else {
            int rs = membershipDAO.deleteById(params);
            System.out.println("방 나가기 완료");
            return "ExitChatroom";
        }

    }

    public List<ChatUserDTO> getUserList(int userid, int roomid) {

        List<MemberShipDTO> list = membershipDAO.getUserList(roomid);

        System.out.println(list.get(1).getId() + " : " + list.get(1).getEmpId());
        List<ChatUserDTO> memberList = new ArrayList<>();
        for (MemberShipDTO dto : list) {
            System.out.println(dto.getEmpId() + " : 직원고유번호");
            int id = dto.getEmpId();
            EmployeesDTO emp = employeesMapper.findById(id);

            if (emp != null) {
                String name = emp.getName();
                memberList.add(new ChatUserDTO(id, name));
            } else {
                System.out.println("❗ 직원 정보 없음: ID = " + id);
                // 필요하다면 에러 응답 대신 continue 해서 무시
            }
        }
        return memberList;


    }
}
