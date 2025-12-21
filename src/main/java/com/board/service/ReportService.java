package com.board.service;

import com.board.entity.Report;
import com.board.enums.ReportReason;
import com.board.enums.ReportStatus;
import com.board.enums.ReportTargetType;
import com.board.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * 신고 생성
     */
    @Transactional
    public Map<String, Object> createReport(Long reporterId, ReportTargetType targetType, Long targetId,
                                            ReportReason reason, String description) {
        Map<String, Object> result = new HashMap<>();

        // 중복 신고 확인
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporterId, targetType, targetId)) {
            result.put("success", false);
            result.put("message", "이미 신고하셨습니다.");
            return result;
        }

        // 신고 생성
        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setDescription(description);
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);

        result.put("success", true);
        result.put("message", "신고가 접수되었습니다.");
        return result;
    }

    /**
     * 신고 여부 확인
     */
    public boolean isReported(Long reporterId, ReportTargetType targetType, Long targetId) {
        if (reporterId == null) {
            return false;
        }
        return reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporterId, targetType, targetId);
    }

    /**
     * 모든 신고 조회 (관리자용)
     */
    public Page<Report> getAllReports(Pageable pageable) {
        return reportRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 상태별 신고 조회 (관리자용)
     */
    public Page<Report> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    /**
     * 대기중인 신고 개수
     */
    public long getPendingReportCount() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }

    /**
     * 특정 대상에 대한 신고 목록
     */
    public List<Report> getReportsByTarget(ReportTargetType targetType, Long targetId) {
        return reportRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId);
    }

    /**
     * 특정 대상에 대한 신고 개수
     */
    public long getReportCountByTarget(ReportTargetType targetType, Long targetId) {
        return reportRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    /**
     * 신고 승인 (관리자)
     */
    @Transactional
    public void approveReport(Long reportId, Long adminId, String adminComment) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));

        report.setStatus(ReportStatus.APPROVED);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessedBy(adminId);
        report.setAdminComment(adminComment);

        reportRepository.save(report);
    }

    /**
     * 신고 반려 (관리자)
     */
    @Transactional
    public void rejectReport(Long reportId, Long adminId, String adminComment) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));

        report.setStatus(ReportStatus.REJECTED);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessedBy(adminId);
        report.setAdminComment(adminComment);

        reportRepository.save(report);
    }

    /**
     * 신고 상세 조회
     */
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고를 찾을 수 없습니다."));
    }

    /**
     * 사용자가 신고한 목록
     */
    public List<Report> getReportsByReporter(Long reporterId) {
        return reportRepository.findByReporterIdOrderByCreatedAtDesc(reporterId);
    }
}
