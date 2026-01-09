package com.board.enums;

public enum ReactionType {
    LIKE("❤️", "좋아요"),
    HELPFUL("👍", "유익해요"),
    FUNNY("😂", "재미있어요"),
    WOW("😮", "놀라워요"),
    SAD("😢", "슬퍼요"),
    ANGRY("😡", "화나요"),
    THINKING("🤔", "생각중이에요"),
    CELEBRATE("🎉", "축하해요");

    private final String emoji;
    private final String displayName;

    ReactionType(String emoji, String displayName) {
        this.emoji = emoji;
        this.displayName = displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplayName() {
        return displayName;
    }
}
