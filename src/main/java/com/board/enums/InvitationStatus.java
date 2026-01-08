package com.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 초대 상태
 */
@Getter
@RequiredArgsConstructor
public enum InvitationStatus {
    PENDING("대기중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨"),
    EXPIRED("만료됨");

    private final String displayName;
}
