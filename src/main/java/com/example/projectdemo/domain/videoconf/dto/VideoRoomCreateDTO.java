package com.example.projectdemo.domain.videoconf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomCreateDTO {
    private String id;
    private String name;
    private String password;
    private String createdBy;
}