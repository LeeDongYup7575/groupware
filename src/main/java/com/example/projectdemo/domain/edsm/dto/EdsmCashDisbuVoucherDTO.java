package com.example.projectdemo.domain.edsm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EdsmCashDisbuVoucherDTO {

    private int id;
    private int edsmDocumentId;
    private String drafterId;
    private String title;
    private String content;
    private String accountingDate;
    private String spenderId;
    private String spenderName;
    private String spenderPosition;
    private String bank;
    private String bankAccount;

}
