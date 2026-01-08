package com.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 타입
 */
@Getter
@RequiredArgsConstructor
public enum CommunityType {
    PUBLIC("공개"),           // 누구나 조회 및 가입 가능
    PRIVATE("비공개"),        // 누구나 조회 가능하지만 초대받은 사람만 가입 가능
    SECRET("비밀");           // 검색 불가, 초대받은 사람만 조회 및 가입 가능

    private final String displayName;
}
