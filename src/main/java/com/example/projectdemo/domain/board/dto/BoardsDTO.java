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
public class BoardsDTO {

    private Integer id;
    private String name;
    private Integer sortOrder;
    private boolean isGlobal;
    private boolean isActive;
    private String description;
    private LocalDateTime createdAt;

}
