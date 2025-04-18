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
public class PostsDTO {

    private Integer id;
    private Integer empId;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdAt;
    private Integer boardId;
    // 작성자 이름 필드 추가
    private String author;
    // 게시판 이름 필드 추가
    private String boardName;
    // 댓글 수 필드 추가
    private int commentCount;
    //새 게시글 여부를 판단하는 필드 추가
    private boolean newPost;
}
