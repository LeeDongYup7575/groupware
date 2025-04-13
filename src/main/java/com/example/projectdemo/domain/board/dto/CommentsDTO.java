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
public class CommentsDTO {

    private Integer id;
    private Integer postId;
    private Integer empId;
    private String content;
    private LocalDateTime createdAt;
    private Integer parentId;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    // 추가 필드 - 화면 표시용
    private String empName;
    private String profileImgUrl; // 프로필 이미지 URL

    public Boolean getDeleted() {
        return this.isDeleted;
    }
}
