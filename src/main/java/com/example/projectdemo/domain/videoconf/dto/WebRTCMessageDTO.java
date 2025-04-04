package com.example.projectdemo.domain.videoconf.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebRTCMessageDTO {
    private String from;       // 메시지 발신자
    private String type;       // 메시지 타입 (offer, answer, ice, join, leave 등)
    private Object payload;    // SDP 또는 ICE 후보 정보
    private String roomId;     // 화상 채팅방 ID
}
