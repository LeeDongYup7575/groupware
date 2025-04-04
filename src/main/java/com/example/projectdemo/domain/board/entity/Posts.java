package com.example.projectdemo.domain.board.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Posts {

    private Integer id;
    private Integer empId;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdAt;
    private Integer boardId;

}
