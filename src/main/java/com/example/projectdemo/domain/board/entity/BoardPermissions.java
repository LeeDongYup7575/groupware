package com.example.projectdemo.domain.board.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPermissions {

    private Integer id;
    private Integer boardId;
    private Integer empId;
    private String permissionType;
    private LocalDateTime createdAt;

}
