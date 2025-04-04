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
public class PostStarsDTO {

    private Integer id;
    private Integer empId;
    private Integer postId;
    private LocalDateTime createdAt;

}
