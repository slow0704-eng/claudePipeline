package com.board.service;

import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.Like;
import com.board.entity.User;
import com.board.enums.ReactionType;
import com.board.enums.TargetType;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
import com.board.repository.LikeRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public Map<String, Object> toggleLike(TargetType targetType, Long targetId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(
                currentUser.getId(), targetType, targetId);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // Unlike
            likeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            // Like
            Like like = Like.builder().build();
            like.setUserId(currentUser.getId());
            like.setTargetType(targetType);
            like.setTargetId(targetId);
            likeRepository.save(like);
            isLiked = true;
        }

        // Update count - 좋아요 수만 직접 업데이트 (다른 필드에 영향 없음)
        long likeCount = likeRepository.countByTargetTypeAndTargetId(targetType, targetId);

        if (targetType == TargetType.POST) {
            // 좋아요 수만 업데이트 (다른 필드 보존)
            boardRepository.updateLikeCount(targetId, (int) likeCount);

            // 알림을 위해 게시글 정보 조회
            Board board = boardRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

            // Create notification for post like
            if (isLiked && !board.getUserId().equals(currentUser.getId())) {
                notificationService.createNotification(
                    board.getUserId(),
                    "LIKE",
                    "새로운 좋아요",
                    currentUser.getNickname() + "님이 회원님의 게시글을 좋아합니다: " +
                        (board.getTitle().length() > 30 ? board.getTitle().substring(0, 30) + "..." : board.getTitle()),
                    "POST",
                    targetId
                );
            }
        } else if (targetType == TargetType.COMMENT) {
            // 좋아요 수만 업데이트 (다른 필드 보존)
            commentRepository.updateLikeCount(targetId, (int) likeCount);

            // 알림을 위해 댓글 정보 조회
            Comment comment = commentRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

            // Create notification for comment like
            if (isLiked && !comment.getUserId().equals(currentUser.getId())) {
                notificationService.createNotification(
                    comment.getUserId(),
                    "LIKE",
                    "새로운 좋아요",
                    currentUser.getNickname() + "님이 회원님의 댓글을 좋아합니다: " +
                        (comment.getContent().length() > 30 ? comment.getContent().substring(0, 30) + "..." : comment.getContent()),
                    "COMMENT",
                    targetId
                );
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", likeCount);
        return result;
    }

    public boolean isLiked(TargetType targetType, Long targetId, Long userId) {
        if (userId == null) {
            return false;
        }
        return likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    public long getLikeCount(TargetType targetType, Long targetId) {
        return likeRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    public List<Like> getLikedPostsByUserId(Long userId) {
        return likeRepository.findByUserIdAndTargetTypeOrderByCreatedAtDesc(userId, TargetType.POST);
    }

    /**
     * 사용자의 반응 타입 조회
     */
    public ReactionType getUserReaction(TargetType targetType, Long targetId, Long userId) {
        if (userId == null) {
            return null;
        }
        return likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .map(Like::getReactionType)
                .orElse(null);
    }

    /**
     * 반응 타입별 카운트 조회
     */
    public Map<String, Integer> getReactionCounts(TargetType targetType, Long targetId) {
        List<Like> likes = likeRepository.findAll().stream()
                .filter(like -> like.getTargetType() == targetType && like.getTargetId().equals(targetId))
                .toList();

        Map<String, Integer> counts = new HashMap<>();
        for (ReactionType type : ReactionType.values()) {
            long count = likes.stream()
                    .filter(like -> like.getReactionType() == type)
                    .count();
            counts.put(type.name(), (int) count);
        }
        return counts;
    }
}
