package com.board.repository;

import com.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdAndIsDeletedFalseOrderByCreatedAtAsc(Long boardId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user WHERE c.boardId = :boardId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findByBoardIdWithUser(@Param("boardId") Long boardId);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByBoardIdAndIsDeletedFalse(Long boardId);

    // 좋아요 수 직접 업데이트 (다른 필드에 영향 없음)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Comment c SET c.likeCount = :likeCount WHERE c.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") int likeCount);

    // 통계용 쿼리 메서드
    long countByCreatedAtAfter(LocalDateTime dateTime);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 대시보드 통계용
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.board ORDER BY c.createdAt DESC")
    List<Comment> findTop10ByOrderByCreatedAtDesc();
}
