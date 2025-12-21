package com.board.repository;

import com.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdAndIsDeletedFalseOrderByCreatedAtAsc(Long boardId);
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByBoardIdAndIsDeletedFalse(Long boardId);

    // 통계용 쿼리 메서드
    long countByCreatedAtAfter(LocalDateTime dateTime);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
