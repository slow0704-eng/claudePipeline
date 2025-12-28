package com.board.service;

import com.board.entity.Board;
import com.board.entity.ExternalShare;
import com.board.entity.Share;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.ExternalShareRepository;
import com.board.repository.ShareRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final ExternalShareRepository externalShareRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * 게시글 공유 토글 (공유/공유 취소)
     */
    @Transactional
    public Map<String, Object> toggleShare(Long boardId, String quoteContent) {
        User currentUser = AuthenticationUtils.getCurrentUser(userService);
        if (currentUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 게시글 존재 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Optional<Share> existingShare = shareRepository.findByUserIdAndBoardId(currentUser.getId(), boardId);

        boolean isShared;
        Share share = null;

        if (existingShare.isPresent()) {
            // 공유 취소
            shareRepository.delete(existingShare.get());
            isShared = false;
        } else {
            // 공유 생성
            share = new Share();
            share.setUserId(currentUser.getId());
            share.setBoardId(boardId);

            if (quoteContent != null && !quoteContent.trim().isEmpty()) {
                share.setQuoteContent(quoteContent.trim());
                share.setShareType(Share.ShareType.QUOTE);
            } else {
                share.setShareType(Share.ShareType.SIMPLE);
            }

            shareRepository.save(share);
            isShared = true;

            // 알림 생성 (자신의 게시글 공유는 제외)
            if (!board.getUserId().equals(currentUser.getId())) {
                String message = share.getShareType() == Share.ShareType.QUOTE
                    ? currentUser.getNickname() + "님이 회원님의 게시글을 인용 공유했습니다: " +
                      (board.getTitle().length() > 30 ? board.getTitle().substring(0, 30) + "..." : board.getTitle())
                    : currentUser.getNickname() + "님이 회원님의 게시글을 공유했습니다: " +
                      (board.getTitle().length() > 30 ? board.getTitle().substring(0, 30) + "..." : board.getTitle());

                notificationService.createNotification(
                    board.getUserId(),
                    "SHARE",
                    "게시글 공유",
                    message,
                    "POST",
                    boardId
                );
            }
        }

        long shareCount = shareRepository.countByBoardId(boardId);

        Map<String, Object> result = new HashMap<>();
        result.put("isShared", isShared);
        result.put("shareCount", shareCount);
        if (share != null) {
            result.put("share", share);
        }

        return result;
    }

    /**
     * 특정 게시글의 공유 수 조회
     */
    public long getShareCount(Long boardId) {
        return shareRepository.countByBoardId(boardId);
    }

    /**
     * 사용자가 게시글을 공유했는지 확인
     */
    public boolean isShared(Long boardId, Long userId) {
        if (userId == null) {
            return false;
        }
        return shareRepository.existsByUserIdAndBoardId(userId, boardId);
    }

    /**
     * 특정 게시글을 공유한 사용자 목록
     */
    public List<Map<String, Object>> getShareUsers(Long boardId) {
        List<Share> shares = shareRepository.findByBoardIdOrderByCreatedAtDesc(boardId);

        return shares.stream().map(share -> {
            User user = userService.findById(share.getUserId());
            Map<String, Object> shareInfo = new HashMap<>();
            shareInfo.put("shareId", share.getId());
            shareInfo.put("userId", user.getId());
            shareInfo.put("nickname", user.getNickname());
            shareInfo.put("quoteContent", share.getQuoteContent());
            shareInfo.put("shareType", share.getShareType());
            shareInfo.put("createdAt", share.getCreatedAt());
            return shareInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 공유한 게시글 목록
     */
    public List<Share> getSharedBoardsByUserId(Long userId) {
        return shareRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 공유 상세 정보 조회
     */
    public Map<String, Object> getShareInfo(Long boardId, Long userId) {
        Map<String, Object> info = new HashMap<>();
        info.put("shareCount", getShareCount(boardId));
        info.put("isShared", isShared(boardId, userId));
        return info;
    }

    // ========== 외부 공유 통계 메서드 ==========

    /**
     * 외부 공유 추적 (Twitter, Facebook, LinkedIn, Kakao, Link Copy, QR Code)
     */
    @Transactional
    public void trackExternalShare(Long boardId, ExternalShare.SharePlatform platform,
                                   Long userId, String ipAddress, String userAgent) {
        // 게시글 존재 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        ExternalShare share = new ExternalShare();
        share.setBoardId(boardId);
        share.setPlatform(platform);
        share.setUserId(userId);  // 비로그인 사용자의 경우 null
        share.setIpAddress(ipAddress);
        share.setUserAgent(userAgent);

        externalShareRepository.save(share);
    }

    /**
     * 특정 게시글의 외부 공유 통계 조회
     */
    public Map<String, Object> getShareStatistics(Long boardId) {
        Map<String, Object> stats = new HashMap<>();

        // 내부 공유 수 (타임라인 공유)
        long internalShares = shareRepository.countByBoardId(boardId);
        stats.put("internalShares", internalShares);

        // 외부 공유 총 수
        long totalExternalShares = externalShareRepository.countByBoardId(boardId);
        stats.put("totalExternalShares", totalExternalShares);

        // 전체 공유 수
        stats.put("totalShares", internalShares + totalExternalShares);

        // 플랫폼별 공유 통계
        List<Object[]> platformStats = externalShareRepository.findPlatformStatsByBoardId(boardId);
        Map<String, Long> platformBreakdown = new HashMap<>();

        for (Object[] stat : platformStats) {
            ExternalShare.SharePlatform platform = (ExternalShare.SharePlatform) stat[0];
            Long count = (Long) stat[1];
            platformBreakdown.put(platform.name(), count);
        }
        stats.put("platformBreakdown", platformBreakdown);

        // 각 플랫폼별 공유 수 (개별)
        stats.put("twitterShares", externalShareRepository.countByBoardIdAndPlatform(boardId, ExternalShare.SharePlatform.TWITTER));
        stats.put("facebookShares", externalShareRepository.countByBoardIdAndPlatform(boardId, ExternalShare.SharePlatform.FACEBOOK));
        stats.put("linkedinShares", externalShareRepository.countByBoardIdAndPlatform(boardId, ExternalShare.SharePlatform.LINKEDIN));
        stats.put("kakaoShares", externalShareRepository.countByBoardIdAndPlatform(boardId, ExternalShare.SharePlatform.KAKAO));
        stats.put("linkCopyShares", externalShareRepository.countByBoardIdAndPlatform(boardId, ExternalShare.SharePlatform.LINK_COPY));
        stats.put("qrCodeShares", externalShareRepository.countByBoardIdAndPlatform(boardId, ExternalShare.SharePlatform.QR_CODE));

        return stats;
    }

    /**
     * 가장 많이 공유된 게시글 순위 (전체 외부 공유)
     */
    public List<Map<String, Object>> getMostSharedBoards(int limit) {
        List<Object[]> results = externalShareRepository.findMostSharedBoards();

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Long boardId = (Long) result[0];
                    Long shareCount = (Long) result[1];

                    Map<String, Object> item = new HashMap<>();
                    item.put("boardId", boardId);
                    item.put("shareCount", shareCount);

                    // 게시글 정보 조회
                    boardRepository.findById(boardId).ifPresent(board -> {
                        item.put("title", board.getTitle());
                        item.put("author", userService.findById(board.getUserId()).getNickname());
                        item.put("createdAt", board.getCreatedAt());
                    });

                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 가장 많이 공유된 게시글 순위 (특정 플랫폼)
     */
    public List<Map<String, Object>> getMostSharedBoardsByPlatform(ExternalShare.SharePlatform platform, int limit) {
        List<Object[]> results = externalShareRepository.findMostSharedBoardsByPlatform(platform);

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Long boardId = (Long) result[0];
                    Long shareCount = (Long) result[1];

                    Map<String, Object> item = new HashMap<>();
                    item.put("boardId", boardId);
                    item.put("platform", platform.name());
                    item.put("shareCount", shareCount);

                    // 게시글 정보 조회
                    boardRepository.findById(boardId).ifPresent(board -> {
                        item.put("title", board.getTitle());
                        item.put("author", userService.findById(board.getUserId()).getNickname());
                    });

                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 기간 동안의 공유 통계
     */
    public Map<String, Object> getShareStatsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = externalShareRepository.findShareStatsByPeriod(startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        Map<String, Long> platformCounts = new HashMap<>();
        long total = 0;

        for (Object[] result : results) {
            ExternalShare.SharePlatform platform = (ExternalShare.SharePlatform) result[0];
            Long count = (Long) result[1];
            platformCounts.put(platform.name(), count);
            total += count;
        }

        stats.put("platformCounts", platformCounts);
        stats.put("totalShares", total);
        stats.put("startDate", startDate);
        stats.put("endDate", endDate);

        return stats;
    }

    /**
     * 최근 외부 공유 목록 조회
     */
    public List<Map<String, Object>> getRecentExternalShares(int limit) {
        List<ExternalShare> shares = externalShareRepository.findTop10ByOrderBySharedAtDesc();

        return shares.stream()
                .limit(limit)
                .map(share -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("shareId", share.getId());
                    item.put("boardId", share.getBoardId());
                    item.put("platform", share.getPlatform().name());
                    item.put("sharedAt", share.getSharedAt());

                    // 사용자 정보 (있는 경우)
                    if (share.getUserId() != null) {
                        User user = userService.findById(share.getUserId());
                        item.put("nickname", user.getNickname());
                    }

                    // 게시글 정보
                    boardRepository.findById(share.getBoardId()).ifPresent(board -> {
                        item.put("title", board.getTitle());
                    });

                    return item;
                })
                .collect(Collectors.toList());
    }
}
