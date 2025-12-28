package com.board.repository;

import com.board.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    /**
     * 특정 사용자가 특정 게시글을 공유했는지 확인
     */
    Optional<Share> findByUserIdAndBoardId(Long userId, Long boardId);

    /**
     * 특정 사용자가 특정 게시글을 공유했는지 여부
     */
    boolean existsByUserIdAndBoardId(Long userId, Long boardId);

    /**
     * 특정 게시글의 공유 수
     */
    long countByBoardId(Long boardId);

    /**
     * 특정 게시글을 공유한 사용자 목록 (최근순)
     */
    List<Share> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    /**
     * 특정 사용자가 공유한 게시글 목록 (최근순)
     */
    List<Share> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 게시글을 공유한 사용자 목록 (페이징)
     */
    @Query("SELECT s FROM Share s WHERE s.boardId = :boardId ORDER BY s.createdAt DESC")
    List<Share> findSharesByBoardId(@Param("boardId") Long boardId);

    /**
     * 특정 사용자가 공유한 모든 게시글의 공유 삭제
     */
    void deleteByUserId(Long userId);

    /**
     * 특정 게시글의 모든 공유 삭제
     */
    void deleteByBoardId(Long boardId);
}
