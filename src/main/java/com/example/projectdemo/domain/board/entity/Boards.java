package com.example.projectdemo.domain.board.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boards {

    private Integer id;
    private String name;
    private Integer sortOrder;
    private boolean isGlobal;
    private boolean isActive;
    private String description;
    private LocalDateTime createdAt;


}
