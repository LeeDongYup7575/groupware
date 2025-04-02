package com.example.projectdemo.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuppliesDTO {
    private Integer id;
    private String name;
    private String category;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String description;
    private Boolean isAvailable;
}