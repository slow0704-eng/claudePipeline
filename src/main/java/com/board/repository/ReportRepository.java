package com.board.repository;

import com.board.entity.Report;
import com.board.enums.ReportStatus;
import com.board.enums.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // 중복 신고 확인
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, ReportTargetType targetType, Long targetId);

    // 특정 신고 조회
    Optional<Report> findByReporterIdAndTargetTypeAndTargetId(Long reporterId, ReportTargetType targetType, Long targetId);

    // 상태별 신고 조회 (페이징)
    Page<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    // 모든 신고 조회 (최신순)
    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 대기중인 신고 개수
    long countByStatus(ReportStatus status);

    // 특정 대상에 대한 신고 목록
    List<Report> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(ReportTargetType targetType, Long targetId);

    // 특정 대상에 대한 신고 개수
    long countByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);

    // 사용자가 신고한 목록
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    // 상태별 신고 조회 (PENDING 등)
    List<Report> findByStatus(String status, org.springframework.data.domain.Pageable pageable);

    // 통계용 쿼리 메서드
    long countByStatus(String status);

    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    // 대시보드 통계용
    List<Report> findTop10ByStatusOrderByCreatedAtDesc(ReportStatus status);
}
