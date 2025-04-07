package com.example.projectdemo.domain.videoconf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomJoinDTO {
    private String empNum;
    private String empName;
    private String deptName;
    private String roomId;
    private String roomName;
    private String roomPassword;
}