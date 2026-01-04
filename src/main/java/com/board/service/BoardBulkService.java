package com.board.service;

import com.board.entity.Board;
import com.board.enums.BoardStatus;
import com.board.exception.ErrorCode;
import com.board.exception.ValidationException;
import com.board.repository.BoardRepository;
import com.board.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 대량 처리 작업을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardBulkService {

    private final BoardRepository boardRepository;
    private final BoardCrudService boardCrudService;
    private final FileUploadService fileUploadService;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void bulkUpdateStatus(List<Long> boardIds, BoardStatus newStatus) {
        if (boardIds == null || boardIds.isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE, "게시글 ID 목록이 비어있습니다.");
        }

        for (Long boardId : boardIds) {
            Board board = boardCrudService.getBoardById(boardId);
            board.setStatus(newStatus);
            boardRepository.save(board);
        }
    }

    @Transactional
    public void bulkSoftDelete(List<Long> boardIds) {
        bulkUpdateStatus(boardIds, BoardStatus.DELETED);
    }

    @Transactional
    public void bulkHide(List<Long> boardIds) {
        bulkUpdateStatus(boardIds, BoardStatus.HIDDEN);
    }

    @Transactional
    public void bulkRestore(List<Long> boardIds) {
        bulkUpdateStatus(boardIds, BoardStatus.PUBLIC);
    }

    @Transactional
    public void bulkHardDelete(List<Long> boardIds) {
        if (boardIds == null || boardIds.isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_INPUT_VALUE, "게시글 ID 목록이 비어있습니다.");
        }

        for (Long boardId : boardIds) {
            Board board = boardCrudService.getBoardById(boardId);
            // 첨부파일 삭제
            fileUploadService.deleteFilesByBoardId(boardId);
            // 북마크 삭제
            bookmarkRepository.deleteByBoardId(boardId);
            // 게시글 삭제
            boardRepository.delete(board);
        }
    }

    @Transactional
    public Board updateBoardStatus(Long boardId, BoardStatus newStatus) {
        Board board = boardCrudService.getBoardById(boardId);
        board.setStatus(newStatus);
        return boardRepository.save(board);
    }

    @Transactional
    public Board updateBoardCategory(Long boardId, Long categoryId) {
        Board board = boardCrudService.getBoardById(boardId);
        board.setCategoryId(categoryId);
        return boardRepository.save(board);
    }
}
