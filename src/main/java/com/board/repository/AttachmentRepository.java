package com.board.repository;

import com.board.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // 게시글 ID로 첨부파일 조회
    List<Attachment> findByBoardId(Long boardId);

    // 게시글 ID로 첨부파일 개수 조회
    long countByBoardId(Long boardId);

    // 게시글 ID로 첨부파일 삭제
    void deleteByBoardId(Long boardId);
}
