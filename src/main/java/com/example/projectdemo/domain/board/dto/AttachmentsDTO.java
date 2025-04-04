package com.example.projectdemo.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentsDTO {

    private Integer id;
    private Integer postId;
    private String originName;
    private String sysName;
    private LocalDateTime uploadedAt;

}
