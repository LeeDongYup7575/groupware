package com.example.projectdemo.domain.employees.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentsDTO {
    private Integer id;
    private String name;
    private Integer sortOrder;
}
