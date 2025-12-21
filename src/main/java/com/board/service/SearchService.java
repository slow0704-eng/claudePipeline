package com.board.service;

import com.board.entity.Board;
import com.board.entity.SearchHistory;
import com.board.repository.BoardRepository;
import com.board.repository.SearchHistoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BoardRepository boardRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final EntityManager entityManager;

    /**
     * 통합 검색 (제목 + 내용 + 작성자)
     */
    public Page<Board> search(String keyword, int page, int size, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }

        Pageable pageable;
        if ("latest".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if ("views".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        } else if ("likes".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        } else {
            // 기본은 관련도순 (최신순으로 대체)
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return boardRepository.searchByKeyword(keyword.trim(), pageable);
    }

    /**
     * 상세 필터를 포함한 검색
     */
    public Page<Board> searchWithFilters(
            String keyword, String searchType, String sortBy,
            int page, int size,
            String dateFrom, String dateTo,
            Integer minViews, Integer maxViews,
            Integer minLikes, Integer maxLikes,
            Integer minComments, Integer maxComments) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Board> query = cb.createQuery(Board.class);
        Root<Board> board = query.from(Board.class);

        List<Predicate> predicates = new ArrayList<>();

        // 1. isDraft = false (공개 게시글만)
        predicates.add(cb.isFalse(board.get("isDraft")));

        // 2. 키워드 검색 (searchType에 따라)
        String trimmedKeyword = keyword.trim();
        Predicate keywordPredicate;

        switch (searchType) {
            case "title":
                keywordPredicate = cb.like(board.get("title"), "%" + trimmedKeyword + "%");
                break;
            case "content":
                keywordPredicate = cb.like(board.get("content"), "%" + trimmedKeyword + "%");
                break;
            case "author":
                keywordPredicate = cb.like(board.get("nickname"), "%" + trimmedKeyword + "%");
                break;
            default: // "all"
                keywordPredicate = cb.or(
                        cb.like(board.get("title"), "%" + trimmedKeyword + "%"),
                        cb.like(board.get("content"), "%" + trimmedKeyword + "%"),
                        cb.like(board.get("nickname"), "%" + trimmedKeyword + "%")
                );
        }
        predicates.add(keywordPredicate);

        // 3. 날짜 범위 필터
        if (dateFrom != null && !dateFrom.isEmpty()) {
            try {
                LocalDateTime startDateTime = LocalDate.parse(dateFrom).atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(board.get("createdAt"), startDateTime));
            } catch (Exception ignored) {}
        }

        if (dateTo != null && !dateTo.isEmpty()) {
            try {
                LocalDateTime endDateTime = LocalDate.parse(dateTo).atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(board.get("createdAt"), endDateTime));
            } catch (Exception ignored) {}
        }

        // 4. 조회수 범위 필터
        if (minViews != null && minViews >= 0) {
            predicates.add(cb.greaterThanOrEqualTo(board.get("viewCount"), minViews));
        }
        if (maxViews != null && maxViews >= 0) {
            predicates.add(cb.lessThanOrEqualTo(board.get("viewCount"), maxViews));
        }

        // 5. 좋아요 범위 필터
        if (minLikes != null && minLikes >= 0) {
            predicates.add(cb.greaterThanOrEqualTo(board.get("likeCount"), minLikes));
        }
        if (maxLikes != null && maxLikes >= 0) {
            predicates.add(cb.lessThanOrEqualTo(board.get("likeCount"), maxLikes));
        }

        // 6. 댓글수 범위 필터
        if (minComments != null && minComments >= 0) {
            predicates.add(cb.greaterThanOrEqualTo(board.get("commentCount"), minComments));
        }
        if (maxComments != null && maxComments >= 0) {
            predicates.add(cb.lessThanOrEqualTo(board.get("commentCount"), maxComments));
        }

        // 모든 조건 AND로 결합
        query.where(predicates.toArray(new Predicate[0]));

        // 7. 정렬
        Order order;
        switch (sortBy) {
            case "views":
                order = cb.desc(board.get("viewCount"));
                break;
            case "likes":
                order = cb.desc(board.get("likeCount"));
                break;
            case "comments":
                order = cb.desc(board.get("commentCount"));
                break;
            default: // "latest"
                order = cb.desc(board.get("createdAt"));
        }
        query.orderBy(order);

        // 8. 페이징 처리
        List<Board> results = entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        // 9. 전체 개수 쿼리
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Board> countRoot = countQuery.from(Board.class);
        countQuery.select(cb.count(countRoot));

        // 동일한 조건 적용
        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(cb.isFalse(countRoot.get("isDraft")));

        Predicate countKeywordPredicate;
        switch (searchType) {
            case "title":
                countKeywordPredicate = cb.like(countRoot.get("title"), "%" + trimmedKeyword + "%");
                break;
            case "content":
                countKeywordPredicate = cb.like(countRoot.get("content"), "%" + trimmedKeyword + "%");
                break;
            case "author":
                countKeywordPredicate = cb.like(countRoot.get("nickname"), "%" + trimmedKeyword + "%");
                break;
            default:
                countKeywordPredicate = cb.or(
                        cb.like(countRoot.get("title"), "%" + trimmedKeyword + "%"),
                        cb.like(countRoot.get("content"), "%" + trimmedKeyword + "%"),
                        cb.like(countRoot.get("nickname"), "%" + trimmedKeyword + "%")
                );
        }
        countPredicates.add(countKeywordPredicate);

        if (dateFrom != null && !dateFrom.isEmpty()) {
            try {
                LocalDateTime startDateTime = LocalDate.parse(dateFrom).atStartOfDay();
                countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdAt"), startDateTime));
            } catch (Exception ignored) {}
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            try {
                LocalDateTime endDateTime = LocalDate.parse(dateTo).atTime(LocalTime.MAX);
                countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdAt"), endDateTime));
            } catch (Exception ignored) {}
        }
        if (minViews != null && minViews >= 0) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("viewCount"), minViews));
        }
        if (maxViews != null && maxViews >= 0) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("viewCount"), maxViews));
        }
        if (minLikes != null && minLikes >= 0) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("likeCount"), minLikes));
        }
        if (maxLikes != null && maxLikes >= 0) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("likeCount"), maxLikes));
        }
        if (minComments != null && minComments >= 0) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("commentCount"), minComments));
        }
        if (maxComments != null && maxComments >= 0) {
            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("commentCount"), maxComments));
        }

        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // 10. Page 객체 생성
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 제목으로만 검색
     */
    public Page<Board> searchByTitle(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return boardRepository.findByTitleContainingAndIsDraftFalse(keyword.trim(), pageable);
    }

    /**
     * 내용으로만 검색
     */
    public Page<Board> searchByContent(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return boardRepository.findByContentContainingAndIsDraftFalse(keyword.trim(), pageable);
    }

    /**
     * 작성자로만 검색
     */
    public Page<Board> searchByAuthor(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return boardRepository.findByNicknameContainingAndIsDraftFalse(keyword.trim(), pageable);
    }

    /**
     * 검색어 자동완성 제안
     */
    public List<String> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty() || keyword.length() < 2) {
            return new ArrayList<>();
        }

        // 최근 7일간의 검색 히스토리에서 자동완성
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        List<String> suggestions = searchHistoryRepository.findKeywordSuggestions(
            keyword.trim(), fromDate
        );

        // 게시글 제목에서도 자동완성
        List<String> titleSuggestions = boardRepository.findTitleSuggestionsStartingWith(keyword.trim());

        // 중복 제거하고 합치기
        suggestions.addAll(titleSuggestions);
        return suggestions.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 검색 히스토리 저장
     */
    @Transactional
    public void saveSearchHistory(Long userId, String keyword) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword.trim());
        searchHistoryRepository.save(history);
    }

    /**
     * 사용자 검색 히스토리 조회
     */
    public List<String> getUserSearchHistory(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        return searchHistoryRepository.findTop10ByUserIdOrderBySearchedAtDesc(userId)
                .stream()
                .map(SearchHistory::getKeyword)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 인기 검색어 조회 (최근 7일)
     */
    public List<String> getPopularKeywords(int limit) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        List<Object[]> results = searchHistoryRepository.findPopularKeywords(fromDate);

        return results.stream()
                .map(row -> (String) row[0])
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 검색 히스토리 삭제
     */
    @Transactional
    public void clearUserSearchHistory(Long userId) {
        if (userId != null) {
            searchHistoryRepository.deleteByUserId(userId);
        }
    }

    /**
     * 오래된 검색 히스토리 정리 (30일 이상)
     */
    @Transactional
    public void cleanupOldSearchHistory() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        searchHistoryRepository.deleteBySearchedAtBefore(cutoffDate);
    }

    /**
     * 검색 결과에서 키워드 하이라이팅을 위한 정보 제공
     */
    public String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null || keyword.trim().isEmpty()) {
            return text;
        }

        String escapedKeyword = keyword.trim().replaceAll("([\\[\\](){}.*+?^$|\\\\])", "\\\\$1");
        return text.replaceAll("(?i)(" + escapedKeyword + ")", "<mark>$1</mark>");
    }
}
