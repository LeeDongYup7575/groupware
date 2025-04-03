package com.example.projectdemo.domain.edsm.enums;

public enum ApprovalStatus {

    APPROVED("승인"),
    PENDING("대기"),
    REJECTED("반려");

    private final String label;

    ApprovalStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
