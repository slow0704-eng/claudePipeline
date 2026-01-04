package com.board.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 Enum
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR("COMMON_001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE("COMMON_002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED("COMMON_003", "지원하지 않는 HTTP 메서드입니다."),
    ACCESS_DENIED("COMMON_004", "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME("USER_002", "이미 사용중인 아이디입니다."),
    DUPLICATE_EMAIL("USER_003", "이미 사용중인 이메일입니다."),
    INVALID_PASSWORD("USER_004", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_DELETED("USER_005", "이미 탈퇴한 사용자입니다."),
    DUPLICATE_NICKNAME("USER_006", "이미 사용중인 닉네임입니다."),

    // Board
    BOARD_NOT_FOUND("BOARD_001", "게시글을 찾을 수 없습니다."),
    BOARD_ALREADY_DELETED("BOARD_002", "이미 삭제된 게시글입니다."),
    UNAUTHORIZED_BOARD_ACCESS("BOARD_003", "게시글 수정 권한이 없습니다."),
    BOARD_ALREADY_PUBLISHED("BOARD_004", "이미 발행된 게시글입니다."),
    MAX_PINNED_BOARDS_EXCEEDED("BOARD_005", "최대 3개까지만 고정할 수 있습니다."),

    // Comment
    COMMENT_NOT_FOUND("COMMENT_001", "댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_DELETED("COMMENT_002", "이미 삭제된 댓글입니다."),
    UNAUTHORIZED_COMMENT_ACCESS("COMMENT_003", "댓글 수정 권한이 없습니다."),

    // Like
    ALREADY_LIKED("LIKE_001", "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND("LIKE_002", "좋아요를 찾을 수 없습니다."),

    // Bookmark
    ALREADY_BOOKMARKED("BOOKMARK_001", "이미 북마크한 게시글입니다."),
    BOOKMARK_NOT_FOUND("BOOKMARK_002", "북마크를 찾을 수 없습니다."),

    // File
    FILE_NOT_FOUND("FILE_001", "파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED("FILE_002", "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE("FILE_003", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED("FILE_004", "파일 크기가 제한을 초과했습니다."),

    // Hashtag
    HASHTAG_NOT_FOUND("HASHTAG_001", "해시태그를 찾을 수 없습니다."),
    INVALID_HASHTAG_FORMAT("HASHTAG_002", "잘못된 해시태그 형식입니다."),

    // Message
    MESSAGE_NOT_FOUND("MESSAGE_001", "쪽지를 찾을 수 없습니다."),
    CANNOT_SEND_TO_SELF("MESSAGE_002", "자기 자신에게 쪽지를 보낼 수 없습니다."),

    // Report
    REPORT_NOT_FOUND("REPORT_001", "신고 내역을 찾을 수 없습니다."),
    ALREADY_REPORTED("REPORT_002", "이미 신고한 게시글/댓글입니다."),
    CANNOT_REPORT_OWN_CONTENT("REPORT_003", "자신의 게시글/댓글은 신고할 수 없습니다."),

    // Category
    CATEGORY_NOT_FOUND("CATEGORY_001", "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXISTS("CATEGORY_002", "이미 존재하는 카테고리 이름입니다."),

    // Topic
    TOPIC_NOT_FOUND("TOPIC_001", "토픽을 찾을 수 없습니다."),
    TOPIC_ALREADY_EXISTS("TOPIC_002", "이미 존재하는 토픽 이름입니다."),
    INVALID_TOPIC_LEVEL("TOPIC_003", "잘못된 토픽 레벨입니다."),
    TOPIC_MERGE_FAILED("TOPIC_004", "토픽 병합에 실패했습니다."),

    // Follow
    FOLLOW_NOT_FOUND("FOLLOW_001", "팔로우 관계를 찾을 수 없습니다."),
    ALREADY_FOLLOWING("FOLLOW_002", "이미 팔로우 중입니다."),
    CANNOT_FOLLOW_SELF("FOLLOW_003", "자기 자신을 팔로우할 수 없습니다."),

    // Share
    SHARE_NOT_FOUND("SHARE_001", "공유를 찾을 수 없습니다."),
    ALREADY_SHARED("SHARE_002", "이미 공유한 게시글입니다."),

    // Role
    ROLE_NOT_FOUND("ROLE_001", "역할을 찾을 수 없습니다."),
    ROLE_ALREADY_EXISTS("ROLE_002", "이미 존재하는 역할 이름입니다."),

    // Menu
    MENU_NOT_FOUND("MENU_001", "메뉴를 찾을 수 없습니다."),
    MENU_ALREADY_EXISTS("MENU_002", "이미 존재하는 메뉴 경로입니다."),

    // BannedWord
    BANNED_WORD_NOT_FOUND("BANNEDWORD_001", "금지어를 찾을 수 없습니다."),
    BANNED_WORD_ALREADY_EXISTS("BANNEDWORD_002", "이미 존재하는 금지어입니다."),

    // Notification
    NOTIFICATION_NOT_FOUND("NOTIFICATION_001", "알림을 찾을 수 없습니다."),

    // Attachment
    ATTACHMENT_NOT_FOUND("ATTACHMENT_001", "첨부파일을 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
