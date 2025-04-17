package com.example.projectdemo.domain.videoconf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebRTCMessageDTO {
    private String from;       // 메시지 발신자
    private String empNum;     // 사원 번호
    private String deptName;   // 부서 이름
    private String type;       // 메시지 타입 (offer, answer, ice, join, leave, heartbeat 등)
    private Object payload;    // SDP, ICE 후보 또는 기타 데이터
    private String roomId;     // 화상 채팅방 ID
    private String roomName;   // 방 이름
    private String to;         // 메시지 수신자 (특정 사용자에게만 전송 시)
}