package com.board.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 외부 플랫폼 공유 추적 엔티티
 * - Twitter, Facebook, LinkedIn, KakaoTalk 등 외부 공유 통계
 */
@Entity
@Table(name = "external_share",
       indexes = @Index(name = "idx_board_platform", columnList = "board_id, platform"))
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public SharePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(SharePlatform platform) {
        this.platform = platform;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalShare that = (ExternalShare) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExternalShare{" +
                "id=" + id +
                ", userId=" + userId +
                ", boardId=" + boardId +
                ", platform=" + platform +
                ", sharedAt=" + sharedAt +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
