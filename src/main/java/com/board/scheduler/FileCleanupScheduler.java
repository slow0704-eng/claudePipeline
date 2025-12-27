package com.board.scheduler;

import com.board.config.FileStorageConfig;
import com.board.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 파일 자동 정리 스케줄러
 *
 * 주요 기능:
 * 1. 30일 이상 된 고아 파일 자동 삭제
 * 2. 삭제 전 자동 백업
 * 3. 정리 결과 로그 기록
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    prefix = "file.storage",
    name = "auto-cleanup-enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class FileCleanupScheduler {

    private final FileStorageService fileStorageService;
    private final FileStorageConfig fileStorageConfig;

    /**
     * 고아 파일 자동 정리
     * 기본 스케줄: 매일 새벽 3시 실행
     */
    @Scheduled(cron = "${file.storage.auto-cleanup-cron:0 0 3 * * ?}")
    public void cleanupOrphanedFiles() {
        if (!fileStorageConfig.getAutoCleanupEnabled()) {
            log.info("Auto cleanup is disabled. Skipping...");
            return;
        }

        log.info("Starting automatic file cleanup...");
        log.info("Cleanup criteria: Files older than {} days", fileStorageConfig.getAutoCleanupDays());

        try {
            Map<String, Object> result = fileStorageService.cleanupOldOrphanedFiles(
                fileStorageConfig.getAutoCleanupDays()
            );

            int deletedCount = (int) result.get("deletedCount");
            long deletedSize = (long) result.get("deletedSize");
            int backupCount = (int) result.get("backupCount");
            String backupPath = (String) result.get("backupPath");

            log.info("File cleanup completed successfully!");
            log.info("Deleted files: {}", deletedCount);
            log.info("Deleted size: {}", result.get("deletedSizeFormatted"));
            log.info("Backed up files: {}", backupCount);
            log.info("Backup location: {}", backupPath);

            if (deletedCount > 0) {
                log.warn("Warning: {} orphaned files were deleted. Total size: {}",
                    deletedCount, result.get("deletedSizeFormatted"));
            }

        } catch (Exception e) {
            log.error("Error occurred during file cleanup", e);
        }
    }

    /**
     * 스토리지 상태 체크 (매일 자정)
     * 디스크 사용량과 DB 동기화 상태를 확인
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkStorageStatus() {
        log.info("Checking storage status...");

        try {
            Map<String, Object> stats = fileStorageService.getStorageStatistics();
            Map<String, Object> syncStatus = fileStorageService.checkSyncStatus();

            log.info("Total storage usage: {}", stats.get("totalUsageFormatted"));
            log.info("Total files: {}", stats.get("totalFiles"));
            log.info("Orphaned files: {}", stats.get("orphanedFiles"));
            log.info("Orphaned size: {}", stats.get("orphanedSizeFormatted"));
            log.info("DB/Disk sync status: {}", (boolean) syncStatus.get("isSynced") ? "SYNCED" : "NOT SYNCED");
            log.info("Disk usage: {}", syncStatus.get("diskUsageFormatted"));

            // 동기화 문제 경고
            if (!(boolean) syncStatus.get("isSynced")) {
                log.warn("Warning: DB and disk storage are not in sync!");
                log.warn("Difference: {}", syncStatus.get("differenceFormatted"));
            }

            // 고아 파일 경고
            int orphanedFiles = (int) stats.get("orphanedFiles");
            if (orphanedFiles > 10) {
                log.warn("Warning: There are {} orphaned files taking up {}",
                    orphanedFiles, stats.get("orphanedSizeFormatted"));
            }

        } catch (Exception e) {
            log.error("Error occurred while checking storage status", e);
        }
    }

    /**
     * 수동 실행용 메서드
     */
    public Map<String, Object> runManualCleanup(int days) {
        log.info("Manual cleanup requested for files older than {} days", days);
        return fileStorageService.cleanupOldOrphanedFiles(days);
    }
}
