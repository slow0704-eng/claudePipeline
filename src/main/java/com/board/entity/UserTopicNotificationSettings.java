package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 사용자 토픽 알림 설정 엔티티
 * - 토픽별 알림 설정 관리
 * - 실시간/일간/주간 알림 빈도 설정
 */
@Entity
@Table(name = "user_topic_notification_settings")
@Data
public class UserTopicNotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 ID (유니크)
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 전역 알림 활성화
     */
    @Column(name = "global_notification_enabled", nullable = false)
    private Boolean globalNotificationEnabled = true;

    /**
     * 전역 알림 빈도: REALTIME, DAILY, WEEKLY, NONE
     */
    @Column(name = "global_frequency", length = 20)
    private String globalFrequency = "REALTIME";

    /**
     * 이메일 알림 활성화
     */
    @Column(name = "global_email_enabled", nullable = false)
    private Boolean globalEmailEnabled = false;

    /**
     * 토픽별 개별 설정 (JSONB)
     * 형식: {"topicId": {"enabled": true, "frequency": "DAILY", "emailEnabled": false}}
     */
    @Column(name = "topic_specific_settings", columnDefinition = "TEXT")
    private String topicSpecificSettings = "{}";

    /**
     * 선호 알림 시간 (일간/주간 다이제스트용)
     */
    @Column(name = "preferred_time")
    private LocalTime preferredTime = LocalTime.of(9, 0); // 기본 09:00

    /**
     * 선호 요일 (주간 다이제스트용, 1=월요일, 7=일요일)
     */
    @Column(name = "preferred_day_of_week")
    private Integer preferredDayOfWeek = 1; // 기본 월요일

    /**
     * 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 알림 빈도 상수
     */
    public static final String FREQUENCY_REALTIME = "REALTIME";
    public static final String FREQUENCY_DAILY = "DAILY";
    public static final String FREQUENCY_WEEKLY = "WEEKLY";
    public static final String FREQUENCY_NONE = "NONE";
}
