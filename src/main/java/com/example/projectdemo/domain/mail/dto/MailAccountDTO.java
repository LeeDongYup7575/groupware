package com.example.projectdemo.domain.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailAccountDTO {
    private String accountaddress;
    private String accountpassword;
    private Integer accountid;
}
