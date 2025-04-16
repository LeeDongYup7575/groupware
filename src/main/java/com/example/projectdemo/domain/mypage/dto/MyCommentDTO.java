package com.example.projectdemo.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentDTO {
    private Integer id;
    private Integer postId;
    private String content;
    private LocalDateTime createdAt;
    private String postTitle;   // 게시글 제목 매핑용
}
