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

    // 추가 필드 - 연관 데이터
    private String empName; // 사용자 이름 (JOIN 쿼리 결과용)
    private String empPosition; // 사용자 직급 (JOIN 쿼리 결과용)
    private String empDepartment; // 사용자 부서 (JOIN 쿼리 결과용)
}
