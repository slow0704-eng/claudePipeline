package com.board.enums;

public enum ReportReason {
    SPAM("스팸/도배"),
    INAPPROPRIATE("부적절한 내용"),
    COPYRIGHT("저작권 침해"),
    COMMERCIAL("상업적 광고"),
    OTHER("기타");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
