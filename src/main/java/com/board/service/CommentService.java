package com.board.service;

import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.User;
import com.board.event.CommentCreatedEvent;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.exception.ResourceNotFoundException;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Comment createComment(Long boardId, String content, Long parentCommentId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        // Validate board exists
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BOARD_NOT_FOUND));

        // Create and save comment
        Comment comment = Comment.builder().build();
        comment.setBoardId(boardId);
        comment.setUserId(currentUser.getId());
        comment.setNickname(currentUser.getNickname());
        comment.setContent(content);
        comment.setParentCommentId(parentCommentId);
        comment.setIsDeleted(false);

        Comment savedComment = commentRepository.save(comment);

        // Publish event for async processing (알림 생성 + 댓글 수 증가)
        CommentCreatedEvent event = new CommentCreatedEvent(
            currentUser.getId(),
            savedComment.getId(),
            boardId,
            currentUser.getId(),
            parentCommentId,
            content
        );
        eventPublisher.publishEvent(event);

        return savedComment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        comment.setIsDeleted(true);
        comment.setContent("[삭제된 댓글입니다]");
        commentRepository.save(comment);

        // Decrement board's comment count
        Board board = boardRepository.findById(comment.getBoardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BOARD_NOT_FOUND));
        board.setCommentCount(Math.max(0, board.getCommentCount() - 1));
        boardRepository.save(board);
    }

    public List<Comment> getCommentsTreeByBoardId(Long boardId) {
        // Use Fetch Join to prevent N+1 query problem
        List<Comment> allComments = commentRepository.findByBoardIdWithUser(boardId);

        // Build tree structure
        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> rootComments = new ArrayList<>();

        // First pass: create map
        for (Comment comment : allComments) {
            commentMap.put(comment.getId(), comment);
            comment.setReplies(new ArrayList<>());
        }

        // Second pass: build tree
        for (Comment comment : allComments) {
            if (comment.getParentCommentId() == null) {
                rootComments.add(comment);
            } else {
                Comment parent = commentMap.get(comment.getParentCommentId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            }
        }

        return rootComments;
    }

    public long getCommentCountByBoardId(Long boardId) {
        return commentRepository.countByBoardIdAndIsDeletedFalse(boardId);
    }

    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public boolean isOwner(Comment comment, User user) {
        if (comment == null || user == null) {
            return false;
        }
        return comment.getUserId().equals(user.getId());
    }
}
