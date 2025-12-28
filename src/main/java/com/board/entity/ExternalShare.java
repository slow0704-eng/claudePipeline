package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 외부 플랫폼 공유 추적 엔티티
 * - Twitter, Facebook, LinkedIn, KakaoTalk 등 외부 공유 통계
 */
@Entity
@Table(name = "external_share",
       indexes = @Index(name = "idx_board_platform", columnList = "board_id, platform"))
@Data
public class ExternalShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 공유한 사용자 ID (선택 사항 - 비로그인 사용자도 공유 가능)
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 공유된 게시글 ID
     */
    @Column(nullable = false, name = "board_id")
    private Long boardId;

    /**
     * 공유 플랫폼
     * - TWITTER, FACEBOOK, LINKEDIN, KAKAO, LINK_COPY, QR_CODE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SharePlatform platform;

    /**
     * 공유 일시
     */
    @CreationTimestamp
    @Column(updatable = false, name = "shared_at")
    private LocalDateTime sharedAt;

    /**
     * IP 주소 (중복 공유 방지용 - 선택 사항)
     */
    @Column(length = 45, name = "ip_address")
    private String ipAddress;

    /**
     * User Agent (통계용 - 선택 사항)
     */
    @Column(length = 500, name = "user_agent")
    private String userAgent;

    /**
     * 공유 플랫폼 열거형
     */
    public enum SharePlatform {
        TWITTER,      // Twitter/X
        FACEBOOK,     // Facebook
        LINKEDIN,     // LinkedIn
        KAKAO,        // KakaoTalk
        LINK_COPY,    // 링크 복사
        QR_CODE       // QR 코드 생성
    }
}
