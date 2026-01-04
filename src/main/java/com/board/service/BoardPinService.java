package com.board.service;

import com.board.entity.Board;
import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 고정(Pin) 및 중요 표시 기능을 담당하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardPinService {

    private final BoardRepository boardRepository;
    private final BoardCrudService boardCrudService;

    @Transactional
    @CacheEvict(value = "pinnedBoards", allEntries = true)
    public Board pinBoard(Long boardId, LocalDateTime pinnedUntil) {
        // 현재 고정된 게시글 개수 확인 (최대 3개)
        long pinnedCount = boardRepository.countActivePinnedBoards(LocalDateTime.now());
        if (pinnedCount >= 3) {
            throw new BusinessException(ErrorCode.MAX_PINNED_BOARDS_EXCEEDED, "기존 고정 게시글을 먼저 해제해주세요.");
        }

        Board board = boardCrudService.getBoardById(boardId);
        board.setIsPinned(true);
        board.setPinnedUntil(pinnedUntil);
        return boardRepository.save(board);
    }

    @Transactional
    @CacheEvict(value = "pinnedBoards", allEntries = true)
    public Board unpinBoard(Long boardId) {
        Board board = boardCrudService.getBoardById(boardId);
        board.setIsPinned(false);
        board.setPinnedUntil(null);
        return boardRepository.save(board);
    }

    @Transactional
    public Board toggleImportant(Long boardId) {
        Board board = boardCrudService.getBoardById(boardId);
        board.setIsImportant(!board.getIsImportant());
        return boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pinnedBoards", key = "'active'")
    public List<Board> getActivePinnedBoards() {
        return boardRepository.findActivePinnedBoards(LocalDateTime.now());
    }

    /**
     * 고정 기간이 만료된 게시글 자동 해제
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    @CacheEvict(value = "pinnedBoards", allEntries = true)
    public void unpinExpiredBoards() {
        List<Board> expiredBoards = boardRepository.findExpiredPinnedBoards(LocalDateTime.now());
        if (!expiredBoards.isEmpty()) {
            for (Board board : expiredBoards) {
                board.setIsPinned(false);
                board.setPinnedUntil(null);
                boardRepository.save(board);
            }
            log.info("Unpinned {} expired boards", expiredBoards.size());
        }
    }
}
