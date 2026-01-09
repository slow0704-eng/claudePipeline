package com.board.service;

import com.board.entity.Board;
import com.board.entity.Topic;
import com.board.entity.User;
import com.board.entity.UserTopicNotificationSettings;
import com.board.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 토픽 알림 서비스
 * - 실시간/일간/주간 알림 생성
 * - 다이제스트 이메일 발송
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TopicNotificationService {

    private final UserTopicNotificationSettingsRepository settingsRepository;
    private final UserTopicFollowRepository userTopicFollowRepository;
    private final BoardTopicRepository boardTopicRepository;
    private final BoardRepository boardRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Autowired(required = false)
    private EmailService emailService;

    /**
     * 일간 다이제스트 발송
     * 스케줄러에서 호출
     */
    public void sendDailyDigest() {
        if (emailService == null) {
            log.warn("EmailService가 설정되지 않아 일간 다이제스트를 발송할 수 없습니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);

        // 일간 다이제스트 대상 사용자 조회
        List<UserTopicNotificationSettings> users = settingsRepository.findByGlobalFrequency(
            UserTopicNotificationSettings.FREQUENCY_DAILY
        );

        log.info("일간 다이제스트 발송 시작: 대상 사용자 {}명", users.size());

        for (UserTopicNotificationSettings settings : users) {
            try {
                sendUserDailyDigest(settings.getUserId(), yesterday, now);
            } catch (Exception e) {
                log.error("일간 다이제스트 발송 실패: userId={}", settings.getUserId(), e);
            }
        }

        log.info("일간 다이제스트 발송 완료");
    }

    /**
     * 주간 다이제스트 발송
     * 스케줄러에서 호출
     */
    public void sendWeeklyDigest() {
        if (emailService == null) {
            log.warn("EmailService가 설정되지 않아 주간 다이제스트를 발송할 수 없습니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusDays(7);

        // 주간 다이제스트 대상 사용자 조회
        List<UserTopicNotificationSettings> users = settingsRepository.findByGlobalFrequency(
            UserTopicNotificationSettings.FREQUENCY_WEEKLY
        );

        log.info("주간 다이제스트 발송 시작: 대상 사용자 {}명", users.size());

        for (UserTopicNotificationSettings settings : users) {
            try {
                sendUserWeeklyDigest(settings.getUserId(), lastWeek, now);
            } catch (Exception e) {
                log.error("주간 다이제스트 발송 실패: userId={}", settings.getUserId(), e);
            }
        }

        log.info("주간 다이제스트 발송 완료");
    }

    /**
     * 사용자별 일간 다이제스트 발송
     */
    private void sendUserDailyDigest(Long userId, LocalDateTime since, LocalDateTime until) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getEmail() == null) {
            return;
        }

        // 팔로우한 토픽 목록
        List<Long> followedTopicIds = userTopicFollowRepository.findTopicIdsByUserId(userId);
        if (followedTopicIds.isEmpty()) {
            return;
        }

        // 각 토픽의 새 게시글 수집
        Map<Long, List<Board>> topicBoards = collectNewBoardsByTopics(followedTopicIds, since, until);

        if (topicBoards.isEmpty()) {
            log.debug("일간 다이제스트: 새 게시글 없음, userId={}", userId);
            return;
        }

        // 다이제스트 HTML 생성
        String digestContent = buildDigestHtml(topicBoards);

        // 이메일 발송
        emailService.sendDailyDigestEmail(user.getEmail(), digestContent);
        log.info("일간 다이제스트 발송 완료: userId={}, email={}", userId, user.getEmail());
    }

    /**
     * 사용자별 주간 다이제스트 발송
     */
    private void sendUserWeeklyDigest(Long userId, LocalDateTime since, LocalDateTime until) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getEmail() == null) {
            return;
        }

        // 팔로우한 토픽 목록
        List<Long> followedTopicIds = userTopicFollowRepository.findTopicIdsByUserId(userId);
        if (followedTopicIds.isEmpty()) {
            return;
        }

        // 각 토픽의 새 게시글 수집
        Map<Long, List<Board>> topicBoards = collectNewBoardsByTopics(followedTopicIds, since, until);

        if (topicBoards.isEmpty()) {
            log.debug("주간 다이제스트: 새 게시글 없음, userId={}", userId);
            return;
        }

        // 다이제스트 HTML 생성
        String digestContent = buildDigestHtml(topicBoards);

        // 이메일 발송
        emailService.sendWeeklyDigestEmail(user.getEmail(), digestContent);
        log.info("주간 다이제스트 발송 완료: userId={}, email={}", userId, user.getEmail());
    }

    /**
     * 토픽별 새 게시글 수집
     */
    private Map<Long, List<Board>> collectNewBoardsByTopics(List<Long> topicIds, LocalDateTime since, LocalDateTime until) {
        Map<Long, List<Board>> result = new HashMap<>();

        for (Long topicId : topicIds) {
            // 토픽의 게시글 ID 조회
            List<Long> boardIds = boardTopicRepository.findBoardIdsByTopicId(topicId);

            // 기간 내 게시글 필터링
            List<Board> newBoards = boardIds.stream()
                .map(boardId -> boardRepository.findById(boardId).orElse(null))
                .filter(Objects::nonNull)
                .filter(board -> !board.getIsDraft())
                .filter(board -> board.getCreatedAt().isAfter(since) && board.getCreatedAt().isBefore(until))
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(10) // 토픽당 최대 10개
                .collect(Collectors.toList());

            if (!newBoards.isEmpty()) {
                result.put(topicId, newBoards);
            }
        }

        return result;
    }

    /**
     * 다이제스트 HTML 생성
     */
    private String buildDigestHtml(Map<Long, List<Board>> topicBoards) {
        StringBuilder html = new StringBuilder();

        for (Map.Entry<Long, List<Board>> entry : topicBoards.entrySet()) {
            Long topicId = entry.getKey();
            List<Board> boards = entry.getValue();

            Topic topic = topicRepository.findById(topicId).orElse(null);
            if (topic == null) continue;

            html.append("<div style=\"margin-bottom: 30px;\">");
            html.append("<h3>").append(topic.getIcon() != null ? topic.getIcon() + " " : "")
                .append(topic.getName()).append(" (").append(boards.size()).append("개)</h3>");
            html.append("<ul>");

            for (Board board : boards) {
                html.append("<li>");
                html.append("<a href=\"http://localhost:8080/board/").append(board.getId()).append("\">")
                    .append(board.getTitle()).append("</a>");
                html.append(" <span style=\"color: #999; font-size: 12px;\">- ")
                    .append(board.getNickname()).append("</span>");
                html.append("</li>");
            }

            html.append("</ul>");
            html.append("</div>");
        }

        return html.toString();
    }

    /**
     * 사용자의 알림 설정 조회 (없으면 기본값 생성)
     */
    @Transactional
    public UserTopicNotificationSettings getUserSettings(Long userId) {
        return settingsRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserTopicNotificationSettings settings = new UserTopicNotificationSettings();
                settings.setUserId(userId);
                settings.setGlobalNotificationEnabled(true);
                settings.setGlobalFrequency(UserTopicNotificationSettings.FREQUENCY_REALTIME);
                settings.setGlobalEmailEnabled(false);
                return settingsRepository.save(settings);
            });
    }

    /**
     * 알림 설정 업데이트
     */
    @Transactional
    public UserTopicNotificationSettings updateUserSettings(Long userId, UserTopicNotificationSettings newSettings) {
        UserTopicNotificationSettings settings = getUserSettings(userId);

        settings.setGlobalNotificationEnabled(newSettings.getGlobalNotificationEnabled());
        settings.setGlobalFrequency(newSettings.getGlobalFrequency());
        settings.setGlobalEmailEnabled(newSettings.getGlobalEmailEnabled());

        if (newSettings.getPreferredTime() != null) {
            settings.setPreferredTime(newSettings.getPreferredTime());
        }

        if (newSettings.getPreferredDayOfWeek() != null) {
            settings.setPreferredDayOfWeek(newSettings.getPreferredDayOfWeek());
        }

        if (newSettings.getTopicSpecificSettings() != null) {
            settings.setTopicSpecificSettings(newSettings.getTopicSpecificSettings());
        }

        return settingsRepository.save(settings);
    }
}
