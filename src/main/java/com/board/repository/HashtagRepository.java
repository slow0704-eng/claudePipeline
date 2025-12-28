package com.board.repository;

import com.board.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    /**
     * 이름으로 해시태그 찾기
     */
    Optional<Hashtag> findByName(String name);

    /**
     * 이름으로 해시태그 존재 여부 확인
     */
    boolean existsByName(String name);

    /**
     * 이름이 특정 문자열로 시작하는 해시태그 찾기 (자동완성용)
     */
    List<Hashtag> findTop10ByNameStartingWithOrderByUseCountDesc(String prefix);

    /**
     * 인기 해시태그 조회 (사용 횟수 기준)
     */
    List<Hashtag> findTop20ByOrderByUseCountDesc();

    /**
     * 최근 사용된 해시태그 조회
     */
    @Query("SELECT h FROM Hashtag h WHERE h.lastUsedAt IS NOT NULL ORDER BY h.lastUsedAt DESC")
    List<Hashtag> findRecentlyUsedHashtags();

    /**
     * 최근 24시간 내 사용된 트렌딩 해시태그
     */
    @Query("SELECT h FROM Hashtag h WHERE h.lastUsedAt >= :since ORDER BY h.useCount DESC")
    List<Hashtag> findTrendingHashtags(@Param("since") LocalDateTime since);

    /**
     * 사용 횟수가 0인 해시태그 삭제 (정리용)
     */
    @Query("DELETE FROM Hashtag h WHERE h.useCount = 0")
    void deleteUnusedHashtags();

    /**
     * 특정 사용 횟수 이상의 해시태그 조회
     */
    @Query("SELECT h FROM Hashtag h WHERE h.useCount >= :minCount ORDER BY h.useCount DESC")
    List<Hashtag> findPopularHashtags(@Param("minCount") Long minCount);

    /**
     * 전체 해시태그 통계
     */
    @Query("SELECT COUNT(h), SUM(h.useCount) FROM Hashtag h")
    List<Object[]> getTotalStatistics();

    /**
     * 가장 인기있는 해시태그 Top N
     */
    @Query("SELECT h FROM Hashtag h ORDER BY h.useCount DESC")
    List<Hashtag> findTopHashtags();
}
