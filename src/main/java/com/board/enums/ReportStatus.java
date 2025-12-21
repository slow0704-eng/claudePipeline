package com.board.enums;

public enum ReportStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("반려됨");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
