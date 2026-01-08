package com.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 모더레이션 액션 타입
 */
@Getter
@RequiredArgsConstructor
public enum ModerationActionType {
    MEMBER_ROLE_CHANGED("멤버 역할 변경"),
    MEMBER_KICKED("멤버 강제 퇴출"),
    MEMBER_BANNED("멤버 차단"),
    BOARD_DELETED("게시글 삭제"),
    BOARD_HIDDEN("게시글 숨김"),
    COMMENT_DELETED("댓글 삭제"),
    CATEGORY_CREATED("카테고리 생성"),
    CATEGORY_UPDATED("카테고리 수정"),
    CATEGORY_DELETED("카테고리 삭제"),
    SETTINGS_UPDATED("커뮤니티 설정 변경");

    private final String displayName;
}
