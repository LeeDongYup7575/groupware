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
    private Integer sortOrder;
    private boolean isGlobal;
    private boolean isActive;
    private String description;
    private LocalDateTime createdAt;

    // 그룹 게시판일 경우 권한 설정을 위한 필드
    private List<Integer> memberIds; // 멤버 ID 목록
    private List<String> permissions; // 각 멤버별 권한 (읽기, 쓰기, 없음)

}
