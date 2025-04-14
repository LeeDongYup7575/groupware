package com.example.projectdemo.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DepartmentListDTO {
    private int id;
    private String name;
    private int count;
}
