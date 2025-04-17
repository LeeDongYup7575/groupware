package com.example.projectdemo.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserDTO {
    private Integer id;
    private String name;
    private String profileImgUrl;

}
