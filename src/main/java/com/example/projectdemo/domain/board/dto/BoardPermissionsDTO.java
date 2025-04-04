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
public class BoardPermissionsDTO {

    private Integer id;
    private Integer boardId;
    private Integer empId;
    private String permissionType;
    private LocalDateTime createdAt;

}
