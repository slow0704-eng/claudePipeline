package com.board.service;

import com.board.entity.Board;
import com.board.entity.Comment;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.CommentRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Comment createComment(Long boardId, String content, Long parentCommentId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Comment comment = new Comment();
        comment.setBoardId(boardId);
        comment.setUserId(currentUser.getId());
        comment.setNickname(currentUser.getNickname());
        comment.setContent(content);
        comment.setParentCommentId(parentCommentId);
        comment.setIsDeleted(false);

        Comment savedComment = commentRepository.save(comment);

        // Increment board's comment count
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        board.setCommentCount(board.getCommentCount() + 1);
        boardRepository.save(board);

        // Create notification
        if (parentCommentId != null) {
            // Reply to comment - notify parent comment author
            Comment parentComment = commentRepository.findById(parentCommentId).orElse(null);
            if (parentComment != null && !parentComment.getUserId().equals(currentUser.getId())) {
                notificationService.createNotification(
                    parentComment.getUserId(),
                    "REPLY",
                    "새로운 답글",
                    currentUser.getNickname() + "님이 회원님의 댓글에 답글을 남겼습니다: " +
                        (content.length() > 50 ? content.substring(0, 50) + "..." : content),
                    "COMMENT",
                    savedComment.getId()
                );
            }
        } else {
            // Comment on post - notify post author
            if (!board.getUserId().equals(currentUser.getId())) {
                notificationService.createNotification(
                    board.getUserId(),
                    "COMMENT",
                    "새로운 댓글",
                    currentUser.getNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다: " +
                        (content.length() > 50 ? content.substring(0, 50) + "..." : content),
                    "POST",
                    boardId
                );
            }
        }

        return savedComment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("자신의 댓글만 삭제할 수 있습니다.");
        }

        comment.setIsDeleted(true);
        comment.setContent("[삭제된 댓글입니다]");
        commentRepository.save(comment);

        // Decrement board's comment count
        Board board = boardRepository.findById(comment.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        board.setCommentCount(Math.max(0, board.getCommentCount() - 1));
        boardRepository.save(board);
    }

    public List<Comment> getCommentsTreeByBoardId(Long boardId) {
        List<Comment> allComments = commentRepository.findByBoardIdAndIsDeletedFalseOrderByCreatedAtAsc(boardId);

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
