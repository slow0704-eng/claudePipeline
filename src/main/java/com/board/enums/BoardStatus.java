package com.board.enums;

public enum BoardStatus {
    PUBLIC("공개"),          // 일반 공개 게시글
    HIDDEN("숨김"),          // 숨김 처리된 게시글
    DELETED("삭제"),         // 삭제된 게시글 (소프트 삭제)
    PENDING("승인대기");     // 금지어 등으로 승인 대기 중

    private final String displayName;

    BoardStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
