package com.example.projectdemo.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Integer parentId; // 부모 댓글 ID (null이면 최상위 댓글)
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    // 추가 필드 - 화면 표시용
    private String empName;
    private String profileImgUrl; // 프로필 이미지 URL

    private List<CommentsDTO> replies = new ArrayList<>();  // 대댓글 목록
}
