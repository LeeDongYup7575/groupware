package com.example.projectdemo.domain.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class personalContactDTO {
    private int id;
    private int empId;
    private String name;
    private String email;
    private String phone;
    private String memo;
}
