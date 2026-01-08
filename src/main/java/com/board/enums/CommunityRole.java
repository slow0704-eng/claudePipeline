package com.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 멤버 역할
 */
@Getter
@RequiredArgsConstructor
public enum CommunityRole {
    OWNER("소유자"),          // 커뮤니티 생성자 (모든 권한)
    ADMIN("관리자"),          // 커뮤니티 관리 권한 (멤버 관리, 모더레이션)
    MEMBER("멤버");           // 일반 멤버 (게시글 작성, 댓글 작성)

    private final String displayName;
}
