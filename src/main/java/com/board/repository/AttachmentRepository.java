package com.board.repository;

import com.board.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // 게시글 ID로 첨부파일 조회
    List<Attachment> findByBoardId(Long boardId);

    // 게시글 ID로 첨부파일 개수 조회
    long countByBoardId(Long boardId);

    // 게시글 ID로 첨부파일 삭제
    void deleteByBoardId(Long boardId);

    // =============== 스토리지 관리 쿼리 ===============

    // 전체 파일 사용량 (바이트)
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM Attachment a")
    Long getTotalStorageUsage();

    // 전체 파일 개수
    @Query("SELECT COUNT(a) FROM Attachment a")
    Long getTotalFileCount();

    // 대용량 파일 TOP N
    @Query("SELECT a FROM Attachment a ORDER BY a.fileSize DESC")
    List<Attachment> findTopNLargestFiles(org.springframework.data.domain.Pageable pageable);

    // 사용자별 파일 사용량 통계
    @Query("SELECT b.userId, b.nickname, COUNT(a), COALESCE(SUM(a.fileSize), 0) " +
           "FROM Attachment a JOIN Board b ON a.boardId = b.id " +
           "WHERE b.userId IS NOT NULL " +
           "GROUP BY b.userId, b.nickname " +
           "ORDER BY SUM(a.fileSize) DESC")
    List<Object[]> getUserStorageStatistics();

    // 고아 파일 찾기 (게시글이 삭제된 파일)
    @Query("SELECT a FROM Attachment a WHERE a.boardId NOT IN (SELECT b.id FROM Board b)")
    List<Attachment> findOrphanedFiles();

    // 특정 기간 이상 된 파일 조회
    @Query("SELECT a FROM Attachment a WHERE a.uploadedAt < :cutoffDate")
    List<Attachment> findFilesOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 특정 기간 이상 된 고아 파일 조회
    @Query("SELECT a FROM Attachment a WHERE a.uploadedAt < :cutoffDate " +
           "AND a.boardId NOT IN (SELECT b.id FROM Board b)")
    List<Attachment> findOrphanedFilesOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 파일 타입별 통계
    @Query("SELECT a.fileType, COUNT(a), COALESCE(SUM(a.fileSize), 0) " +
           "FROM Attachment a " +
           "GROUP BY a.fileType " +
           "ORDER BY SUM(a.fileSize) DESC")
    List<Object[]> getFileTypeStatistics();

    // 최근 업로드된 파일 조회
    @Query("SELECT a FROM Attachment a ORDER BY a.uploadedAt DESC")
    List<Attachment> findRecentFiles(org.springframework.data.domain.Pageable pageable);
}
