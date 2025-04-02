package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MembershipService {
    @Autowired
    private MembershipDAO membershipDAO;
    @Autowired
    private EmployeesMapper employeesMapper;


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
}
