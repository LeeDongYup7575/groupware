package com.example.projectdemo.domain.notification.enums;

public enum NotificationType {
    COMMENT("댓글"),
    REPLY("답글"),
    PROJECT("프로젝트"),
    TASK("업무"),
    BOOKING("예약"),
    APPROVAL("결재");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationType fromString(String type) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.name().equalsIgnoreCase(type)) {
                return notificationType;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + type);
    }
}