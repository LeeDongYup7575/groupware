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

}
