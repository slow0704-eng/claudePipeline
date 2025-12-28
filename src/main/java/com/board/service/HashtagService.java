package com.board.service;

import com.board.entity.BoardHashtag;
import com.board.entity.Hashtag;
import com.board.repository.BoardHashtagRepository;
import com.board.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final BoardHashtagRepository boardHashtagRepository;

    // 해시태그 패턴: #으로 시작하고 영문, 한글, 숫자, 밑줄 허용
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([가-힣a-zA-Z0-9_]+)");

    /**
     * 텍스트에서 해시태그 추출
     */
    public Set<String> extractHashtags(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new HashSet<>();
        }

        Set<String> hashtags = new HashSet<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);

        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase(); // 소문자로 정규화
            if (tag.length() <= 50) { // 최대 길이 제한
                hashtags.add(tag);
            }
        }

        return hashtags;
    }

    /**
     * 게시글의 해시태그 저장/업데이트
     */
    @Transactional
    public void updateBoardHashtags(Long boardId, String content) {
        // 기존 해시태그 삭제 및 사용 횟수 감소
        removeAllHashtagsFromBoard(boardId);

        // 새로운 해시태그 추출
        Set<String> hashtags = extractHashtags(content);

        // 해시태그 저장 및 게시글과 연결
        for (String tagName : hashtags) {
            Hashtag hashtag = findOrCreateHashtag(tagName);
            linkBoardToHashtag(boardId, hashtag.getId());
        }
    }

    /**
     * 해시태그 찾기 또는 생성
     */
    @Transactional
    public Hashtag findOrCreateHashtag(String name) {
        String normalizedName = name.toLowerCase().trim();

        Optional<Hashtag> existing = hashtagRepository.findByName(normalizedName);
        if (existing.isPresent()) {
            Hashtag hashtag = existing.get();
            hashtag.incrementUseCount();
            return hashtagRepository.save(hashtag);
        } else {
            Hashtag newHashtag = new Hashtag();
            newHashtag.setName(normalizedName);
            newHashtag.setUseCount(1L);
            newHashtag.setLastUsedAt(LocalDateTime.now());
            return hashtagRepository.save(newHashtag);
        }
    }

    /**
     * 게시글과 해시태그 연결
     */
    @Transactional
    public void linkBoardToHashtag(Long boardId, Long hashtagId) {
        if (!boardHashtagRepository.existsByBoardIdAndHashtagId(boardId, hashtagId)) {
            BoardHashtag boardHashtag = new BoardHashtag();
            boardHashtag.setBoardId(boardId);
            boardHashtag.setHashtagId(hashtagId);
            boardHashtagRepository.save(boardHashtag);
        }
    }

    /**
     * 게시글의 모든 해시태그 제거
     */
    @Transactional
    public void removeAllHashtagsFromBoard(Long boardId) {
        List<Long> hashtagIds = boardHashtagRepository.findHashtagIdsByBoardId(boardId);

        // 사용 횟수 감소
        for (Long hashtagId : hashtagIds) {
            hashtagRepository.findById(hashtagId).ifPresent(hashtag -> {
                hashtag.decrementUseCount();
                hashtagRepository.save(hashtag);
            });
        }

        // 관계 삭제
        boardHashtagRepository.deleteByBoardId(boardId);
    }

    /**
     * 특정 게시글의 해시태그 목록 조회
     */
    public List<Hashtag> getBoardHashtags(Long boardId) {
        List<Long> hashtagIds = boardHashtagRepository.findHashtagIdsByBoardId(boardId);
        return hashtagIds.stream()
                .map(id -> hashtagRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 특정 해시태그가 사용된 게시글 ID 목록 조회
     */
    public List<Long> getBoardIdsByHashtag(String tagName) {
        String normalizedName = tagName.toLowerCase().trim().replace("#", "");
        Optional<Hashtag> hashtag = hashtagRepository.findByName(normalizedName);

        if (hashtag.isPresent()) {
            return boardHashtagRepository.findBoardIdsByHashtagId(hashtag.get().getId());
        }

        return new ArrayList<>();
    }

    /**
     * 해시태그 자동완성 검색
     */
    public List<Map<String, Object>> searchHashtags(String prefix) {
        String normalizedPrefix = prefix.toLowerCase().trim().replace("#", "");

        List<Hashtag> hashtags = hashtagRepository
                .findTop10ByNameStartingWithOrderByUseCountDesc(normalizedPrefix);

        return hashtags.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 인기 해시태그 조회
     */
    public List<Map<String, Object>> getPopularHashtags(int limit) {
        List<Hashtag> hashtags = hashtagRepository.findTop20ByOrderByUseCountDesc();

        return hashtags.stream()
                .limit(limit)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    map.put("lastUsedAt", h.getLastUsedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 트렌딩 해시태그 조회 (최근 24시간)
     */
    public List<Map<String, Object>> getTrendingHashtags(int limit) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Hashtag> hashtags = hashtagRepository.findTrendingHashtags(since);

        return hashtags.stream()
                .limit(limit)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    map.put("lastUsedAt", h.getLastUsedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 최근 사용된 해시태그 조회
     */
    public List<Map<String, Object>> getRecentHashtags(int limit) {
        List<Hashtag> hashtags = hashtagRepository.findRecentlyUsedHashtags();

        return hashtags.stream()
                .limit(limit)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    map.put("lastUsedAt", h.getLastUsedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 해시태그 이름으로 조회
     */
    public Optional<Hashtag> findByName(String name) {
        String normalizedName = name.toLowerCase().trim().replace("#", "");
        return hashtagRepository.findByName(normalizedName);
    }

    /**
     * 해시태그 통계 정보 조회
     */
    public Map<String, Object> getHashtagStats(String tagName) {
        String normalizedName = tagName.toLowerCase().trim().replace("#", "");
        Optional<Hashtag> hashtag = hashtagRepository.findByName(normalizedName);

        Map<String, Object> stats = new HashMap<>();
        if (hashtag.isPresent()) {
            Hashtag h = hashtag.get();
            stats.put("name", h.getName());
            stats.put("useCount", h.getUseCount());
            stats.put("createdAt", h.getCreatedAt());
            stats.put("lastUsedAt", h.getLastUsedAt());
            stats.put("postCount", boardHashtagRepository.countByHashtagId(h.getId()));
        }

        return stats;
    }
}
