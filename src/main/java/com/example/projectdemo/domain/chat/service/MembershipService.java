package com.example.projectdemo.domain.chat.service;

import com.example.projectdemo.domain.chat.dao.MembershipDAO;
import com.example.projectdemo.domain.chat.dto.ChatUserDTO;
import com.example.projectdemo.domain.chat.dto.MemberShipDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.mypage.controller.MypageApiController;
import com.example.projectdemo.domain.s3.service.S3Service;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private S3Service s3Service;

    private static final Logger logger = LoggerFactory.getLogger(MypageApiController.class);


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

        if (!isAdmin) { // 👉 관리자(본인이 방 생성자): 방 삭제
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
        } else { // 👉 일반 멤버인 경우: 그냥 "방 나가기"
            membershipDAO.deleteById(params); // 자기 자신만 멤버 리스트에서 제거
            return "ExitChatroom"; // 방은 유지
        }
    }

    // ✅ 채팅방 참여자 리스트 조회
    public List<ChatUserDTO> getUserList(int userid, int roomid) {
        List<MemberShipDTO> list = membershipDAO.getUserList(roomid);
        if (list.isEmpty()) return List.of();

        List<ChatUserDTO> memberList = new ArrayList<>();

        for (MemberShipDTO dto : list) {
            int id = dto.getEmpId();
            EmployeesDTO employee = employeesMapper.findById(id);
            if (employee == null) continue;

            String profileImgUrl = employee.getProfileImgUrl();

            // 기본 프로필로 fallback
            if (profileImgUrl == null || profileImgUrl.equals("/assets/images/default-profile.png")) {
                profileImgUrl = "/assets/images/default-profile.png";
            }

            // 이미 S3 URL이면 그대로 사용
            else if (profileImgUrl.contains("amazonaws.com")) {
                // 그대로 사용
            }

            // S3 변환 시도 (업로드 경로 기반)
            else {
                String fileName = profileImgUrl.substring(profileImgUrl.lastIndexOf("/") + 1);
                String s3Key = "profiles/" + fileName;

                try {
                    if (s3Service.doesObjectExist(s3Key)) {
                        String s3Url = s3Service.getObjectUrl(s3Key);
                        profileImgUrl = s3Url;

                        // DB 업데이트
                        employeesMapper.updateProfileImgUrl(employee.getEmpNum(), s3Url);
                        logger.info("S3 URL로 업데이트됨: {}", s3Url);
                    } else {
                        logger.warn("❌ S3에 존재하지 않음: {}", s3Key);
                        profileImgUrl = "/assets/images/default-profile.png";
                    }
                } catch (Exception e) {
                    logger.warn("❌ S3 변환 실패: {}", e.getMessage());
                    profileImgUrl = "/assets/images/default-profile.png";
                }
            }

            // ✅ 최종적으로 무조건 리스트에 추가
            memberList.add(new ChatUserDTO(employee.getId(), employee.getName(), profileImgUrl));
        }

        for (ChatUserDTO dto : memberList) {
            System.out.println("🟢 사용자: " + dto.getName() + " | 이미지: " + dto.getProfileImgUrl());
        }

        return memberList;
    }


}
