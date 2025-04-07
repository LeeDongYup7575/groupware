package com.example.projectdemo.domain.videoconf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomDTO {
    private String id;
    private String name;
    private int participantsCount;
    private boolean hasPassword;
}