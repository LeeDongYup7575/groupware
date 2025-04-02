package com.example.projectdemo.domain.edsm.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
public enum EdsmStatus {
    progress("진행"),
    approval("승인"),
    companion("반려");


    private final String status;

    EdsmStatus(String status) {
        this.status = status;
    }

    public static EdsmStatus fromString(String text) {
        // Enum 이름 자체로도 매핑 가능하도록
        try {
            return EdsmStatus.valueOf(text);
        } catch (IllegalArgumentException e) {
            // 기존 로직: 한글 상태값으로 매핑
            for (EdsmStatus status : EdsmStatus.values()) {
                if (status.status.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }
}