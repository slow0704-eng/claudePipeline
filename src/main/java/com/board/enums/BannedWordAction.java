package com.board.enums;

public enum BannedWordAction {
    BLOCK("차단"),           // 게시글 작성 차단
    PENDING("승인대기");     // 승인 대기로 전환

    private final String displayName;

    BannedWordAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
