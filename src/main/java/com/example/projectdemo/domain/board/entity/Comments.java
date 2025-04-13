package com.example.projectdemo.domain.board.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public Boolean getDeleted() {
        return this.isDeleted;
    }

}
