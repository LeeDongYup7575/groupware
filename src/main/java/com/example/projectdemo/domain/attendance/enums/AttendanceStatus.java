package com.example.projectdemo.domain.attendance.enums;

public enum AttendanceStatus {
    NORMAL("출근"),
    CHECKOUT("퇴근"),
    LATE("지각"),
    EARLY_LEAVE("조퇴"),
    ABSENT("결근"),
    ANNUAL_LEAVE("연차"),
    SICK_LEAVE("병가"),
    BEFORE_WORK("출근전");

    private final String status;

    AttendanceStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static AttendanceStatus fromString(String text) {
        for (AttendanceStatus status : AttendanceStatus.values()) {
            if (status.status.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}