package com.board.repository;

import com.board.entity.BoardHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardHashtagRepository extends JpaRepository<BoardHashtag, Long> {

    /**
     * 특정 게시글의 모든 해시태그 관계 찾기
     */
    List<BoardHashtag> findByBoardId(Long boardId);

    /**
     * 특정 해시태그가 사용된 모든 게시글 관계 찾기
     */
    List<BoardHashtag> findByHashtagId(Long hashtagId);

    /**
     * 특정 게시글의 해시태그 ID 목록 조회
     */
    @Query("SELECT bh.hashtagId FROM BoardHashtag bh WHERE bh.boardId = :boardId")
    List<Long> findHashtagIdsByBoardId(@Param("boardId") Long boardId);

    /**
     * 특정 해시태그를 사용하는 게시글 ID 목록 조회
     */
    @Query("SELECT bh.boardId FROM BoardHashtag bh WHERE bh.hashtagId = :hashtagId ORDER BY bh.createdAt DESC")
    List<Long> findBoardIdsByHashtagId(@Param("hashtagId") Long hashtagId);

    /**
     * 특정 게시글의 모든 해시태그 관계 삭제
     */
    @Modifying
    @Query("DELETE FROM BoardHashtag bh WHERE bh.boardId = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

    /**
     * 특정 게시글-해시태그 관계 존재 여부 확인
     */
    boolean existsByBoardIdAndHashtagId(Long boardId, Long hashtagId);

    /**
     * 게시글의 해시태그 개수 조회
     */
    long countByBoardId(Long boardId);

    /**
     * 해시태그를 사용하는 게시글 개수 조회
     */
    long countByHashtagId(Long hashtagId);
}
