package com.board.repository;

import com.board.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자가 특정 게시글을 북마크했는지 확인
    boolean existsByUserIdAndBoardId(Long userId, Long boardId);

    // 특정 사용자와 게시글의 북마크 찾기
    Optional<Bookmark> findByUserIdAndBoardId(Long userId, Long boardId);

    // 특정 사용자의 모든 북마크 조회 (최신순)
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 게시글의 북마크 수
    long countByBoardId(Long boardId);

    // 특정 사용자의 북마크 수
    long countByUserId(Long userId);

    // 사용자의 북마크된 게시글 ID 목록 조회
    @Query("SELECT b.boardId FROM Bookmark b WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    List<Long> findBoardIdsByUserId(@Param("userId") Long userId);

    // 게시글 삭제 시 관련 북마크 삭제
    void deleteByBoardId(Long boardId);
}
