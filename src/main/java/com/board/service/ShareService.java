package com.board.service;

import com.board.entity.Board;
import com.board.entity.Share;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.ShareRepository;
import com.board.util.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
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
}
