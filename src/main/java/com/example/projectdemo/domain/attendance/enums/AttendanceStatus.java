package com.example.projectdemo.domain.attendance.enums;

public enum AttendanceStatus {
    NORMAL("출근"),
    CHECKOUT("퇴근"),
    LATE("지각"),
    EARLY_LEAVE("조퇴"),
    ABSENT("결근"),
    ANNUAL_LEAVE("연차"),
    HALF_LEAVE("반차"),
    BEFORE_WORK("미출근");

    private final String status;

    AttendanceStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static AttendanceStatus fromString(String text) {
        // Enum 이름 자체로도 매핑 가능하도록
        try {
            return AttendanceStatus.valueOf(text);
        } catch (IllegalArgumentException e) {
            // 기존 로직: 한글 상태값으로 매핑
            for (AttendanceStatus status : AttendanceStatus.values()) {
                if (status.status.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }
}