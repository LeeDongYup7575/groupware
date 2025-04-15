package com.example.projectdemo.domain.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundcubeContactDTO {
    private Integer contactId;
    private String name;
    private String email;
    private String firstname;
    private String vcard;
    private String words;
    private Integer userId;
    private Integer empId;
}
