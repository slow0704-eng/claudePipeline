package com.board.repository;

import com.board.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // 사용자의 검색 히스토리 조회 (최근 순)
    List<SearchHistory> findTop10ByUserIdOrderBySearchedAtDesc(Long userId);

    // 특정 기간 내 인기 검색어 조회
    @Query("SELECT sh.keyword, COUNT(sh) as cnt FROM SearchHistory sh " +
           "WHERE sh.searchedAt >= :fromDate " +
           "GROUP BY sh.keyword " +
           "ORDER BY cnt DESC, sh.keyword ASC")
    List<Object[]> findPopularKeywords(@Param("fromDate") LocalDateTime fromDate);

    // 검색어 자동완성 (최근 검색어 기반)
    @Query("SELECT DISTINCT sh.keyword FROM SearchHistory sh " +
           "WHERE sh.keyword LIKE CONCAT(:keyword, '%') " +
           "AND sh.searchedAt >= :fromDate " +
           "ORDER BY sh.searchedAt DESC")
    List<String> findKeywordSuggestions(@Param("keyword") String keyword,
                                        @Param("fromDate") LocalDateTime fromDate);

    // 특정 사용자의 검색 히스토리 삭제
    void deleteByUserId(Long userId);

    // 오래된 검색 히스토리 삭제 (예: 30일 이상)
    void deleteBySearchedAtBefore(LocalDateTime date);
}
