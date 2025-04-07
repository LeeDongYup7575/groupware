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
}
