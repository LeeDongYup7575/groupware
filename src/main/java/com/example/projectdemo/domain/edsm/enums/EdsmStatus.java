package com.example.projectdemo.domain.edsm.enums;



public enum EdsmStatus {
    PROGRESS("진행"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String label;

    EdsmStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}