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
    private final com.board.repository.UserHashtagFollowRepository userHashtagFollowRepository;

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

        // 금지된 해시태그 필터링 및 병합된 해시태그 자동 대체
        Set<String> validHashtags = filterAndReplaceHashtags(hashtags);

        // 해시태그 저장 및 게시글과 연결
        for (String tagName : validHashtags) {
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
            Hashtag newHashtag = Hashtag.builder().build();
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

    // ========== 해시태그 팔로우 기능 ==========

    /**
     * 해시태그 팔로우/언팔로우 토글
     */
    @Transactional
    public Map<String, Object> toggleHashtagFollow(Long userId, String tagName) {
        String normalizedName = tagName.toLowerCase().trim().replace("#", "");
        Optional<Hashtag> hashtagOpt = hashtagRepository.findByName(normalizedName);

        if (hashtagOpt.isEmpty()) {
            throw new RuntimeException("존재하지 않는 해시태그입니다.");
        }

        Hashtag hashtag = hashtagOpt.get();
        boolean isFollowing;

        Optional<com.board.entity.UserHashtagFollow> existing =
                userHashtagFollowRepository.findByUserIdAndHashtagId(userId, hashtag.getId());

        if (existing.isPresent()) {
            // 언팔로우
            userHashtagFollowRepository.delete(existing.get());
            isFollowing = false;
        } else {
            // 팔로우
            com.board.entity.UserHashtagFollow follow = new com.board.entity.UserHashtagFollow();
            follow.setUserId(userId);
            follow.setHashtagId(hashtag.getId());
            userHashtagFollowRepository.save(follow);
            isFollowing = true;
        }

        long followerCount = userHashtagFollowRepository.countByHashtagId(hashtag.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        result.put("followerCount", followerCount);
        result.put("hashtagName", hashtag.getName());

        return result;
    }

    /**
     * 사용자가 해시태그를 팔로우하는지 확인
     */
    public boolean isFollowingHashtag(Long userId, Long hashtagId) {
        if (userId == null || hashtagId == null) {
            return false;
        }
        return userHashtagFollowRepository.existsByUserIdAndHashtagId(userId, hashtagId);
    }

    /**
     * 사용자가 팔로우한 해시태그 목록
     */
    public List<Hashtag> getFollowedHashtags(Long userId) {
        List<Long> hashtagIds = userHashtagFollowRepository.findHashtagIdsByUserId(userId);
        return hashtagIds.stream()
                .map(id -> hashtagRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 팔로우한 해시태그의 게시글 ID 목록
     */
    public List<Long> getBoardIdsByFollowedHashtags(Long userId) {
        List<Long> hashtagIds = userHashtagFollowRepository.findHashtagIdsByUserId(userId);
        Set<Long> boardIds = new HashSet<>();

        for (Long hashtagId : hashtagIds) {
            List<Long> ids = boardHashtagRepository.findBoardIdsByHashtagId(hashtagId);
            boardIds.addAll(ids);
        }

        return new ArrayList<>(boardIds);
    }

    /**
     * 해시태그 팔로워 수 조회
     */
    public long getFollowerCount(Long hashtagId) {
        return userHashtagFollowRepository.countByHashtagId(hashtagId);
    }

    /**
     * 해시태그 상세 정보 (통계 + 팔로우 정보 포함)
     */
    public Map<String, Object> getHashtagDetails(String tagName, Long currentUserId) {
        String normalizedName = tagName.toLowerCase().trim().replace("#", "");
        Optional<Hashtag> hashtagOpt = hashtagRepository.findByName(normalizedName);

        Map<String, Object> details = new HashMap<>();

        if (hashtagOpt.isPresent()) {
            Hashtag hashtag = hashtagOpt.get();

            details.put("id", hashtag.getId());
            details.put("name", hashtag.getName());
            details.put("useCount", hashtag.getUseCount());
            details.put("createdAt", hashtag.getCreatedAt());
            details.put("lastUsedAt", hashtag.getLastUsedAt());
            details.put("postCount", boardHashtagRepository.countByHashtagId(hashtag.getId()));
            details.put("followerCount", getFollowerCount(hashtag.getId()));
            details.put("isFollowing", currentUserId != null && isFollowingHashtag(currentUserId, hashtag.getId()));
        }

        return details;
    }

    // ========== 해시태그 분석 기능 ==========

    /**
     * 기간별 트렌딩 해시태그
     */
    public Map<String, List<Map<String, Object>>> getTrendingByPeriod() {
        Map<String, List<Map<String, Object>>> trends = new HashMap<>();

        // 1시간
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        trends.put("1hour", convertToTrendList(hashtagRepository.findTrendingHashtags(oneHourAgo), 10));

        // 24시간
        LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
        trends.put("1day", convertToTrendList(hashtagRepository.findTrendingHashtags(oneDayAgo), 10));

        // 7일
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        trends.put("7days", convertToTrendList(hashtagRepository.findTrendingHashtags(oneWeekAgo), 10));

        // 30일
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        trends.put("30days", convertToTrendList(hashtagRepository.findTrendingHashtags(oneMonthAgo), 10));

        return trends;
    }

    private List<Map<String, Object>> convertToTrendList(List<Hashtag> hashtags, int limit) {
        return hashtags.stream()
                .limit(limit)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    map.put("lastUsedAt", h.getLastUsedAt());
                    map.put("postCount", boardHashtagRepository.countByHashtagId(h.getId()));
                    map.put("followerCount", getFollowerCount(h.getId()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 관련 해시태그 추천 (함께 사용된 해시태그)
     */
    public List<Map<String, Object>> getRelatedHashtags(String tagName, int limit) {
        String normalizedName = tagName.toLowerCase().trim().replace("#", "");
        Optional<Hashtag> hashtagOpt = hashtagRepository.findByName(normalizedName);

        if (hashtagOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Long hashtagId = hashtagOpt.get().getId();
        List<Object[]> relatedIds = boardHashtagRepository.findRelatedHashtagIds(hashtagId);

        return relatedIds.stream()
                .limit(limit)
                .map(result -> {
                    Long relatedId = (Long) result[0];
                    Long frequency = (Long) result[1];

                    Map<String, Object> map = new HashMap<>();
                    hashtagRepository.findById(relatedId).ifPresent(h -> {
                        map.put("id", h.getId());
                        map.put("name", h.getName());
                        map.put("useCount", h.getUseCount());
                        map.put("coOccurrence", frequency); // 함께 사용된 횟수
                        map.put("postCount", boardHashtagRepository.countByHashtagId(h.getId()));
                    });
                    return map;
                })
                .filter(map -> !map.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 워드클라우드 데이터
     */
    public List<Map<String, Object>> getWordCloudData(int limit) {
        List<Object[]> frequencies = boardHashtagRepository.findAllHashtagFrequencies();

        return frequencies.stream()
                .limit(limit)
                .map(result -> {
                    Long id = (Long) result[0];
                    String name = (String) result[1];
                    Long frequency = (Long) result[2];

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("name", name);
                    map.put("value", frequency); // 워드클라우드 크기 결정
                    map.put("frequency", frequency);

                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 전체 해시태그 통계
     */
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 전체 통계
        List<Object[]> totalStats = hashtagRepository.getTotalStatistics();
        if (!totalStats.isEmpty() && totalStats.get(0) != null) {
            Object[] data = totalStats.get(0);
            stats.put("totalHashtags", data[0] != null ? data[0] : 0L);
            stats.put("totalUsage", data[1] != null ? data[1] : 0L);
        } else {
            stats.put("totalHashtags", 0L);
            stats.put("totalUsage", 0L);
        }

        // 상위 해시태그
        List<Hashtag> topHashtags = hashtagRepository.findTopHashtags();
        stats.put("topHashtags", topHashtags.stream()
                .limit(20)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", h.getName());
                    map.put("useCount", h.getUseCount());
                    map.put("postCount", boardHashtagRepository.countByHashtagId(h.getId()));
                    map.put("followerCount", getFollowerCount(h.getId()));
                    return map;
                })
                .collect(Collectors.toList()));

        // 활동도 분석
        LocalDateTime now = LocalDateTime.now();
        long activeToday = hashtagRepository.findTrendingHashtags(now.minusDays(1)).size();
        long activeThisWeek = hashtagRepository.findTrendingHashtags(now.minusDays(7)).size();

        stats.put("activeToday", activeToday);
        stats.put("activeThisWeek", activeThisWeek);

        return stats;
    }

    /**
     * 해시태그별 게시글 수 통계 (차트용)
     */
    public List<Map<String, Object>> getHashtagPostCountStats(int limit) {
        List<Hashtag> hashtags = hashtagRepository.findTop20ByOrderByUseCountDesc();

        return hashtags.stream()
                .limit(limit)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", h.getName());
                    map.put("postCount", boardHashtagRepository.countByHashtagId(h.getId()));
                    map.put("useCount", h.getUseCount());
                    return map;
                })
                .sorted((a, b) -> ((Long) b.get("postCount")).compareTo((Long) a.get("postCount")))
                .collect(Collectors.toList());
    }

    // ========== 해시태그 관리 기능 (관리자용) ==========

    /**
     * 금지 해시태그 설정/해제
     */
    @Transactional
    public Map<String, Object> toggleBanHashtag(String name, boolean banned) {
        Hashtag hashtag = hashtagRepository.findByName(name.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("해시태그를 찾을 수 없습니다."));

        hashtag.setIsBanned(banned);
        hashtagRepository.save(hashtag);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("name", hashtag.getName());
        result.put("isBanned", hashtag.getIsBanned());

        return result;
    }

    /**
     * 해시태그 설명 수정
     */
    @Transactional
    public Map<String, Object> updateHashtagDescription(String name, String description) {
        Hashtag hashtag = hashtagRepository.findByName(name.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("해시태그를 찾을 수 없습니다."));

        hashtag.setDescription(description);
        hashtagRepository.save(hashtag);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("name", hashtag.getName());
        result.put("description", hashtag.getDescription());

        return result;
    }

    /**
     * 해시태그 병합 (sourceHashtag → targetHashtag)
     * 예: #JS → #JavaScript
     */
    @Transactional
    public Map<String, Object> mergeHashtags(String sourceName, String targetName) {
        Hashtag source = hashtagRepository.findByName(sourceName.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("원본 해시태그를 찾을 수 없습니다."));

        Hashtag target = hashtagRepository.findByName(targetName.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("대상 해시태그를 찾을 수 없습니다."));

        if (source.getId().equals(target.getId())) {
            throw new IllegalArgumentException("같은 해시태그는 병합할 수 없습니다.");
        }

        // 원본 해시태그를 사용하는 모든 게시글 관계를 대상 해시태그로 변경
        List<BoardHashtag> sourceBoardHashtags = boardHashtagRepository.findByHashtagId(source.getId());

        for (BoardHashtag bh : sourceBoardHashtags) {
            // 대상 해시태그로 이미 연결되어 있는지 확인
            if (!boardHashtagRepository.existsByBoardIdAndHashtagId(bh.getBoardId(), target.getId())) {
                // 대상 해시태그와 연결
                BoardHashtag newLink = new BoardHashtag();
                newLink.setBoardId(bh.getBoardId());
                newLink.setHashtagId(target.getId());
                boardHashtagRepository.save(newLink);
            }
            // 원본 연결 삭제
            boardHashtagRepository.delete(bh);
        }

        // 원본 사용 횟수를 대상에 합산
        target.setUseCount(target.getUseCount() + source.getUseCount());
        if (source.getLastUsedAt() != null &&
            (target.getLastUsedAt() == null || source.getLastUsedAt().isAfter(target.getLastUsedAt()))) {
            target.setLastUsedAt(source.getLastUsedAt());
        }
        hashtagRepository.save(target);

        // 원본 해시태그를 병합 상태로 표시
        source.setMergedIntoId(target.getId());
        source.setMergedAt(LocalDateTime.now());
        source.setUseCount(0L);
        hashtagRepository.save(source);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sourceName", source.getName());
        result.put("targetName", target.getName());
        result.put("message", source.getName() + " → " + target.getName() + " 병합 완료");

        return result;
    }

    /**
     * 금지된 해시태그 목록 조회
     */
    public List<Map<String, Object>> getBannedHashtags() {
        List<Hashtag> banned = hashtagRepository.findBannedHashtags();

        return banned.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("description", h.getDescription());
                    map.put("useCount", h.getUseCount());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 병합된 해시태그 목록 조회
     */
    public List<Map<String, Object>> getMergedHashtags() {
        List<Hashtag> merged = hashtagRepository.findMergedHashtags();

        return merged.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("mergedIntoId", h.getMergedIntoId());

                    // 병합된 대상 해시태그 이름 조회
                    if (h.getMergedIntoId() != null) {
                        hashtagRepository.findById(h.getMergedIntoId())
                                .ifPresent(target -> map.put("mergedIntoName", target.getName()));
                    }

                    map.put("mergedAt", h.getMergedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 활성 해시태그 목록 조회 (병합되지 않고 금지되지 않은 것)
     */
    public List<Map<String, Object>> getActiveHashtagsForManagement() {
        List<Hashtag> active = hashtagRepository.findActiveHashtags();

        return active.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", h.getId());
                    map.put("name", h.getName());
                    map.put("description", h.getDescription());
                    map.put("useCount", h.getUseCount());
                    map.put("postCount", boardHashtagRepository.countByHashtagId(h.getId()));
                    map.put("followerCount", getFollowerCount(h.getId()));
                    map.put("isBanned", h.getIsBanned());
                    map.put("lastUsedAt", h.getLastUsedAt());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 해시태그 검증 (금지 또는 병합된 해시태그 체크)
     */
    public boolean isHashtagValid(String name) {
        Optional<Hashtag> hashtag = hashtagRepository.findByName(name.toLowerCase());

        if (hashtag.isEmpty()) {
            return true; // 새로운 해시태그는 허용
        }

        Hashtag h = hashtag.get();

        // 금지된 해시태그는 허용하지 않음
        if (Boolean.TRUE.equals(h.getIsBanned())) {
            return false;
        }

        // 병합된 해시태그는 허용하지 않음
        if (h.getMergedIntoId() != null) {
            return false;
        }

        return true;
    }

    /**
     * 금지 또는 병합된 해시태그를 필터링하고 대체 해시태그 제공
     */
    public Set<String> filterAndReplaceHashtags(Set<String> hashtags) {
        Set<String> validHashtags = new HashSet<>();

        for (String tagName : hashtags) {
            Optional<Hashtag> hashtagOpt = hashtagRepository.findByName(tagName.toLowerCase());

            if (hashtagOpt.isEmpty()) {
                // 새로운 해시태그는 그대로 사용
                validHashtags.add(tagName);
                continue;
            }

            Hashtag hashtag = hashtagOpt.get();

            // 금지된 해시태그는 제외
            if (Boolean.TRUE.equals(hashtag.getIsBanned())) {
                continue;
            }

            // 병합된 해시태그는 대상 해시태그로 대체
            if (hashtag.getMergedIntoId() != null) {
                hashtagRepository.findById(hashtag.getMergedIntoId())
                        .ifPresent(target -> validHashtags.add(target.getName()));
            } else {
                validHashtags.add(tagName);
            }
        }

        return validHashtags;
    }
}
