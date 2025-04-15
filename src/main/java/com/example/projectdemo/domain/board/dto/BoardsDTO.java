package com.example.projectdemo.domain.board.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardsDTO {

    private Integer id;
    private String name;
    private Integer sortOrder = 1; // 기본값 1로 설정
    private boolean isGlobal;
    private boolean isActive;
    private String description;
    private LocalDateTime createdAt;
    private Integer totalPosts;  // 전체 게시글 수
    private Integer adminId;    // 관리자 ID

    // 그룹 게시판일 경우 권한 설정을 위한 필드
    private List<Integer> memberIds; // 멤버 ID 목록
    private List<String> permissions; // 각 멤버별 권한 (읽기, 쓰기, 없음)

}
