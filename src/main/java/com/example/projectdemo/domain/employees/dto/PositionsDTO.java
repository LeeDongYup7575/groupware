package com.example.projectdemo.domain.employees.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionsDTO {
    private Integer id;
    private String title;
    private Integer level;
}
