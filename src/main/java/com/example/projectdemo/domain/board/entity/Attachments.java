package com.example.projectdemo.domain.board.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachments {

    private Integer id;
    private Integer postId;
    private String originName;
    private String sysName;
    private LocalDateTime uploadedAt;

}
