package com.board.service;

import com.board.entity.TopicActivityLog;
import com.board.repository.BoardTopicRepository;
import com.board.repository.TopicActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 토픽 활동 로깅 서비스
 * - 사용자의 토픽 관련 활동을 기록
 * - 추천 알고리즘의 데이터 소스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopicActivityLogService {

    private final TopicActivityLogRepository activityLogRepository;
    private final BoardTopicRepository boardTopicRepository;

    /**
     * 게시글 조회 활동 기록 (비동기)
     *
     * @param userId 사용자 ID
     * @param boardId 게시글 ID
     */
    @Async
    @Transactional
    public void logViewActivity(Long userId, Long boardId) {
        if (userId == null || boardId == null) {
            return;
        }

        // 게시글의 토픽 목록 조회
        List<Long> topicIds = boardTopicRepository.findTopicIdsByBoardId(boardId);

        for (Long topicId : topicIds) {
            // 중복 방지: 이미 같은 활동이 있으면 기록하지 않음 (하루 단위)
            // 성능을 위해 중복 체크를 생략하거나, 최근 1시간 이내만 체크
            createActivityLog(userId, topicId, boardId,
                TopicActivityLog.ACTIVITY_TYPE_VIEW,
                TopicActivityLog.SCORE_VIEW);
        }
    }

    /**
     * 게시글 작성 활동 기록
     *
     * @param userId 사용자 ID
     * @param boardId 게시글 ID
     */
    @Transactional
    public void logCreateActivity(Long userId, Long boardId) {
        if (userId == null || boardId == null) {
            return;
        }

        List<Long> topicIds = boardTopicRepository.findTopicIdsByBoardId(boardId);

        for (Long topicId : topicIds) {
            createActivityLog(userId, topicId, boardId,
                TopicActivityLog.ACTIVITY_TYPE_CREATE,
                TopicActivityLog.SCORE_CREATE);
        }
    }

    /**
     * 좋아요 활동 기록
     *
     * @param userId 사용자 ID
     * @param boardId 게시글 ID
     */
    @Transactional
    public void logLikeActivity(Long userId, Long boardId) {
        if (userId == null || boardId == null) {
            return;
        }

        List<Long> topicIds = boardTopicRepository.findTopicIdsByBoardId(boardId);

        for (Long topicId : topicIds) {
            createActivityLog(userId, topicId, boardId,
                TopicActivityLog.ACTIVITY_TYPE_LIKE,
                TopicActivityLog.SCORE_LIKE);
        }
    }

    /**
     * 활동 로그 생성 (공통 메서드)
     */
    private void createActivityLog(Long userId, Long topicId, Long boardId,
                                   String activityType, BigDecimal score) {
        try {
            TopicActivityLog log = new TopicActivityLog();
            log.setUserId(userId);
            log.setTopicId(topicId);
            log.setBoardId(boardId);
            log.setActivityType(activityType);
            log.setActivityScore(score);

            activityLogRepository.save(log);

        } catch (Exception e) {
            // 로깅 실패는 무시 (메인 기능에 영향 없도록)
            log.error("활동 로그 생성 실패: userId={}, topicId={}, boardId={}, type={}",
                userId, topicId, boardId, activityType, e);
        }
    }

    /**
     * 오래된 활동 로그 정리 (90일 이상)
     * 스케줄러에서 호출
     */
    @Transactional
    public void cleanupOldLogs() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        try {
            activityLogRepository.deleteByCreatedAtBefore(cutoffDate);
            log.info("오래된 활동 로그 정리 완료: {} 이전 데이터 삭제", cutoffDate);
        } catch (Exception e) {
            log.error("활동 로그 정리 실패", e);
        }
    }

    /**
     * 사용자의 최근 활동 조회
     */
    @Transactional(readOnly = true)
    public List<TopicActivityLog> getUserRecentActivities(Long userId, int limit) {
        List<TopicActivityLog> activities = activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return activities.stream().limit(limit).toList();
    }

    /**
     * 토픽의 최근 활동 조회
     */
    @Transactional(readOnly = true)
    public List<TopicActivityLog> getTopicRecentActivities(Long topicId, int limit) {
        List<TopicActivityLog> activities = activityLogRepository.findByTopicIdOrderByCreatedAtDesc(topicId);
        return activities.stream().limit(limit).toList();
    }

    /**
     * 사용자의 활동 통계
     */
    @Transactional(readOnly = true)
    public long getUserActivityCount(Long userId) {
        return activityLogRepository.countByUserId(userId);
    }

    /**
     * 토픽의 활동 통계
     */
    @Transactional(readOnly = true)
    public long getTopicActivityCount(Long topicId) {
        return activityLogRepository.countByTopicId(topicId);
    }
}
