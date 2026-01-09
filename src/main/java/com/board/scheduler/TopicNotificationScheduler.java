package com.board.scheduler;

import com.board.service.TopicActivityLogService;
import com.board.service.TopicNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 토픽 알림 스케줄러
 * - 일간/주간 다이제스트 발송
 * - 오래된 활동 로그 정리
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    prefix = "topic.notification",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class TopicNotificationScheduler {

    private final TopicNotificationService notificationService;
    private final TopicActivityLogService activityLogService;

    /**
     * 일간 다이제스트 발송
     * 기본: 매일 오전 9시
     * application.properties에서 변경 가능:
     * topic.notification.daily-cron=0 0 9 * * ?
     */
    @Scheduled(cron = "${topic.notification.daily-cron:0 0 9 * * ?}")
    public void sendDailyDigest() {
        log.info("일간 다이제스트 스케줄러 시작");
        try {
            notificationService.sendDailyDigest();
        } catch (Exception e) {
            log.error("일간 다이제스트 발송 중 오류 발생", e);
        }
    }

    /**
     * 주간 다이제스트 발송
     * 기본: 매주 월요일 오전 9시
     * application.properties에서 변경 가능:
     * topic.notification.weekly-cron=0 0 9 * * MON
     */
    @Scheduled(cron = "${topic.notification.weekly-cron:0 0 9 * * MON}")
    public void sendWeeklyDigest() {
        log.info("주간 다이제스트 스케줄러 시작");
        try {
            notificationService.sendWeeklyDigest();
        } catch (Exception e) {
            log.error("주간 다이제스트 발송 중 오류 발생", e);
        }
    }

    /**
     * 오래된 활동 로그 정리
     * 기본: 매일 새벽 3시
     * 90일 이상 지난 로그 삭제
     */
    @Scheduled(cron = "${topic.notification.cleanup-cron:0 0 3 * * ?}")
    public void cleanupOldActivityLogs() {
        log.info("활동 로그 정리 스케줄러 시작");
        try {
            activityLogService.cleanupOldLogs();
        } catch (Exception e) {
            log.error("활동 로그 정리 중 오류 발생", e);
        }
    }
}
