package com.example.projectdemo.domain.board.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comments {

    private Integer id;
    private Integer postId;
    private Integer empId;
    private String content;
    private LocalDateTime createdAt;
    private Integer parentId; // 대댓글을 위한 필드 (null이면 일반 댓글)
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    // 추가 필드
    private String profileImgUrl;
    private String empName;

    // 대댓글 목록 (계층 구조용)
    @Builder.Default
    private List<Comments> replies = new ArrayList<>();
}
