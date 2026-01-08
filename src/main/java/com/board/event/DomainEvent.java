package com.board.event;

import java.time.LocalDateTime;

/**
 * 모든 도메인 이벤트의 기본 클래스
 */
public abstract class DomainEvent {

    private final LocalDateTime occurredAt;
    private final Long userId;  // 이벤트를 발생시킨 사용자 ID

    protected DomainEvent(Long userId) {
        this.occurredAt = LocalDateTime.now();
        this.userId = userId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public Long getUserId() {
        return userId;
    }
}
