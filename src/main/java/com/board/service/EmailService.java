package com.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 이메일 발송 서비스
 * - 토픽 알림 이메일 발송
 * - HTML 이메일 지원
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    prefix = "spring.mail",
    name = "host",
    matchIfMissing = false
)
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@example.com}")
    private String fromEmail;

    @Value("${app.name:게시판}")
    private String appName;

    /**
     * 텍스트 이메일 발송 (비동기)
     *
     * @param to 수신자 이메일
     * @param subject 제목
     * @param text 내용
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("이메일 발송 완료: to={}, subject={}", to, subject);

        } catch (Exception e) {
            log.error("이메일 발송 실패: to={}, subject={}", to, subject, e);
        }
    }

    /**
     * HTML 이메일 발송 (비동기)
     *
     * @param to 수신자 이메일
     * @param subject 제목
     * @param htmlContent HTML 내용
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("HTML 이메일 발송 완료: to={}, subject={}", to, subject);

        } catch (MessagingException e) {
            log.error("HTML 이메일 발송 실패: to={}, subject={}", to, subject, e);
        }
    }

    /**
     * 토픽 알림 이메일 발송
     *
     * @param to 수신자 이메일
     * @param topicName 토픽 이름
     * @param newPostCount 새 게시글 수
     * @param postTitles 게시글 제목 목록
     */
    public void sendTopicNotificationEmail(String to, String topicName, int newPostCount, java.util.List<String> postTitles) {
        String subject = String.format("[%s] %s 토픽에 새 게시글 %d개", appName, topicName, newPostCount);

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>").append(topicName).append(" 토픽 알림</h2>");
        html.append("<p>팔로우하신 토픽에 새로운 게시글이 게시되었습니다.</p>");
        html.append("<p><strong>새 게시글 수:</strong> ").append(newPostCount).append("개</p>");
        html.append("<h3>최근 게시글:</h3>");
        html.append("<ul>");

        for (String title : postTitles) {
            html.append("<li>").append(title).append("</li>");
        }

        html.append("</ul>");
        html.append("<p><a href=\"http://localhost:8080/topics/feed\">토픽 피드 보기</a></p>");
        html.append("<hr>");
        html.append("<p style=\"color: #999; font-size: 12px;\">이 이메일은 토픽 알림 설정에 따라 발송되었습니다. ");
        html.append("<a href=\"http://localhost:8080/mypage/topic-notifications\">알림 설정 변경하기</a></p>");
        html.append("</body></html>");

        sendHtmlEmail(to, subject, html.toString());
    }

    /**
     * 일간 다이제스트 이메일 발송
     *
     * @param to 수신자 이메일
     * @param digestContent 다이제스트 내용
     */
    public void sendDailyDigestEmail(String to, String digestContent) {
        String subject = String.format("[%s] 토픽 일간 다이제스트", appName);

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>토픽 일간 다이제스트</h2>");
        html.append("<p>오늘 팔로우하신 토픽의 새로운 소식입니다.</p>");
        html.append(digestContent);
        html.append("<p><a href=\"http://localhost:8080/topics/feed\">전체 피드 보기</a></p>");
        html.append("<hr>");
        html.append("<p style=\"color: #999; font-size: 12px;\">일간 다이제스트는 매일 설정하신 시간에 발송됩니다. ");
        html.append("<a href=\"http://localhost:8080/mypage/topic-notifications\">알림 설정 변경하기</a></p>");
        html.append("</body></html>");

        sendHtmlEmail(to, subject, html.toString());
    }

    /**
     * 주간 다이제스트 이메일 발송
     *
     * @param to 수신자 이메일
     * @param digestContent 다이제스트 내용
     */
    public void sendWeeklyDigestEmail(String to, String digestContent) {
        String subject = String.format("[%s] 토픽 주간 다이제스트", appName);

        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>토픽 주간 다이제스트</h2>");
        html.append("<p>이번 주 팔로우하신 토픽의 새로운 소식입니다.</p>");
        html.append(digestContent);
        html.append("<p><a href=\"http://localhost:8080/topics/feed\">전체 피드 보기</a></p>");
        html.append("<hr>");
        html.append("<p style=\"color: #999; font-size: 12px;\">주간 다이제스트는 매주 설정하신 요일과 시간에 발송됩니다. ");
        html.append("<a href=\"http://localhost:8080/mypage/topic-notifications\">알림 설정 변경하기</a></p>");
        html.append("</body></html>");

        sendHtmlEmail(to, subject, html.toString());
    }
}
