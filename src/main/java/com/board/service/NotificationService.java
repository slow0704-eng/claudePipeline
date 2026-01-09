package com.board.service;

import com.board.entity.Notification;
import com.board.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(Long userId, String type, String title, String content,
                                    String referenceType, Long referenceId) {
        Notification notification = Notification.builder().build();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getRecentNotifications(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification notification : notifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }

    /**
     * 댓글 답글 알림 생성
     */
    @Transactional
    public void createCommentReplyNotification(Long userId, Long boardId, Long commentId, Long replyId) {
        createNotification(
            userId,
            "COMMENT_REPLY",
            "새로운 답글",
            "회원님의 댓글에 새로운 답글이 달렸습니다.",
            "COMMENT",
            commentId
        );
    }

    /**
     * 댓글 알림 생성
     */
    @Transactional
    public void createCommentNotification(Long userId, Long boardId, Long commentId) {
        createNotification(
            userId,
            "COMMENT",
            "새로운 댓글",
            "회원님의 게시글에 새로운 댓글이 달렸습니다.",
            "POST",
            boardId
        );
    }
}
