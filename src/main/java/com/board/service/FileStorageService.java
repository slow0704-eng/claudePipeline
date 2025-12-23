package com.board.service;

import com.board.entity.Attachment;
import com.board.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final AttachmentRepository attachmentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 스토리지 전체 통계 조회
     */
    public Map<String, Object> getStorageStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 전체 사용량
        Long totalUsage = attachmentRepository.getTotalStorageUsage();
        Long totalFiles = attachmentRepository.getTotalFileCount();

        stats.put("totalUsage", totalUsage);
        stats.put("totalUsageFormatted", formatFileSize(totalUsage));
        stats.put("totalFiles", totalFiles);

        // 고아 파일 수
        List<Attachment> orphanedFiles = attachmentRepository.findOrphanedFiles();
        Long orphanedSize = orphanedFiles.stream()
                .mapToLong(Attachment::getFileSize)
                .sum();

        stats.put("orphanedFiles", orphanedFiles.size());
        stats.put("orphanedSize", orphanedSize);
        stats.put("orphanedSizeFormatted", formatFileSize(orphanedSize));

        return stats;
    }

    /**
     * 대용량 파일 TOP 10 조회
     */
    public List<Attachment> getTopLargestFiles(int limit) {
        return attachmentRepository.findTopNLargestFiles(PageRequest.of(0, limit));
    }

    /**
     * 사용자별 사용량 통계
     */
    public List<Map<String, Object>> getUserStorageStatistics() {
        List<Object[]> results = attachmentRepository.getUserStorageStatistics();

        return results.stream().map(row -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("userId", row[0]);
            stat.put("nickname", row[1]);
            stat.put("fileCount", row[2]);
            stat.put("totalSize", row[3]);
            stat.put("totalSizeFormatted", formatFileSize((Long) row[3]));
            return stat;
        }).collect(Collectors.toList());
    }

    /**
     * 파일 타입별 통계
     */
    public List<Map<String, Object>> getFileTypeStatistics() {
        List<Object[]> results = attachmentRepository.getFileTypeStatistics();

        return results.stream().map(row -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("fileType", row[0]);
            stat.put("count", row[1]);
            stat.put("totalSize", row[2]);
            stat.put("totalSizeFormatted", formatFileSize((Long) row[2]));
            return stat;
        }).collect(Collectors.toList());
    }

    /**
     * 고아 파일 목록 조회
     */
    public List<Attachment> getOrphanedFiles() {
        return attachmentRepository.findOrphanedFiles();
    }

    /**
     * 최근 업로드된 파일 조회
     */
    public List<Attachment> getRecentFiles(int limit) {
        return attachmentRepository.findRecentFiles(PageRequest.of(0, limit));
    }

    /**
     * 모든 파일 목록 조회
     */
    public List<Attachment> getAllFiles() {
        return attachmentRepository.findAll();
    }

    /**
     * 30일 이상 된 고아 파일 자동 삭제
     */
    @Transactional
    public Map<String, Object> cleanupOldOrphanedFiles(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<Attachment> oldOrphanedFiles = attachmentRepository.findOrphanedFilesOlderThan(cutoffDate);

        Map<String, Object> result = new HashMap<>();
        int deletedCount = 0;
        long deletedSize = 0;
        int backupCount = 0;

        // 백업 디렉토리 생성
        String backupDirPath = uploadDir + "/backup/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        for (Attachment attachment : oldOrphanedFiles) {
            try {
                // 원본 파일 경로
                Path filePath = Paths.get(uploadDir, attachment.getStoredFilePath());

                if (Files.exists(filePath)) {
                    // 백업
                    if (backupFile(filePath, backupDirPath, attachment.getStoredFilePath())) {
                        backupCount++;
                    }

                    // 파일 삭제
                    Files.deleteIfExists(filePath);

                    // 썸네일도 삭제 (있는 경우)
                    String thumbnailPath = attachment.getStoredFilePath().replace(".", "_thumb.");
                    Path thumbPath = Paths.get(uploadDir, thumbnailPath);
                    Files.deleteIfExists(thumbPath);

                    deletedSize += attachment.getFileSize();
                }

                // DB에서 삭제
                attachmentRepository.delete(attachment);
                deletedCount++;

            } catch (IOException e) {
                log.error("Failed to delete file: " + attachment.getStoredFilePath(), e);
            }
        }

        result.put("deletedCount", deletedCount);
        result.put("deletedSize", deletedSize);
        result.put("deletedSizeFormatted", formatFileSize(deletedSize));
        result.put("backupCount", backupCount);
        result.put("backupPath", backupDirPath);

        return result;
    }

    /**
     * 파일 백업
     */
    private boolean backupFile(Path sourcePath, String backupDirPath, String relativeFilePath) {
        try {
            Path backupPath = Paths.get(backupDirPath, relativeFilePath);
            Files.createDirectories(backupPath.getParent());
            Files.copy(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            log.error("Failed to backup file: " + sourcePath, e);
            return false;
        }
    }

    /**
     * 특정 파일 삭제 (관리자용)
     */
    @Transactional
    public void deleteFile(Long attachmentId) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        // 물리적 파일 삭제
        Path filePath = Paths.get(uploadDir, attachment.getStoredFilePath());
        Files.deleteIfExists(filePath);

        // 썸네일 삭제
        String thumbnailPath = attachment.getStoredFilePath().replace(".", "_thumb.");
        Path thumbPath = Paths.get(uploadDir, thumbnailPath);
        Files.deleteIfExists(thumbPath);

        // DB에서 삭제
        attachmentRepository.delete(attachment);
    }

    /**
     * 여러 파일 일괄 삭제
     */
    @Transactional
    public Map<String, Object> deleteMultipleFiles(List<Long> attachmentIds) {
        Map<String, Object> result = new HashMap<>();
        int deletedCount = 0;
        long deletedSize = 0;

        for (Long id : attachmentIds) {
            try {
                Attachment attachment = attachmentRepository.findById(id).orElse(null);
                if (attachment != null) {
                    deletedSize += attachment.getFileSize();
                    deleteFile(id);
                    deletedCount++;
                }
            } catch (Exception e) {
                log.error("Failed to delete file with id: " + id, e);
            }
        }

        result.put("deletedCount", deletedCount);
        result.put("deletedSize", deletedSize);
        result.put("deletedSizeFormatted", formatFileSize(deletedSize));

        return result;
    }

    /**
     * 실제 디스크 사용량 계산
     */
    public long calculateActualDiskUsage() {
        File uploadDirectory = new File(uploadDir);
        return calculateDirectorySize(uploadDirectory);
    }

    /**
     * 디렉토리 크기 재귀 계산
     */
    private long calculateDirectorySize(File directory) {
        long size = 0;
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += calculateDirectorySize(file);
                    }
                }
            }
        }
        return size;
    }

    /**
     * 파일 크기를 읽기 쉬운 형식으로 변환
     */
    public String formatFileSize(long size) {
        if (size < 0) {
            return "0 B";
        }

        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size;

        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }

    /**
     * DB와 실제 파일 시스템 동기화 상태 확인
     */
    public Map<String, Object> checkSyncStatus() {
        Map<String, Object> status = new HashMap<>();

        // DB의 총 파일 크기
        Long dbTotalSize = attachmentRepository.getTotalStorageUsage();

        // 실제 디스크 사용량
        long diskUsage = calculateActualDiskUsage();

        status.put("dbTotalSize", dbTotalSize);
        status.put("dbTotalSizeFormatted", formatFileSize(dbTotalSize));
        status.put("diskUsage", diskUsage);
        status.put("diskUsageFormatted", formatFileSize(diskUsage));
        status.put("difference", Math.abs(diskUsage - dbTotalSize));
        status.put("differenceFormatted", formatFileSize(Math.abs(diskUsage - dbTotalSize)));
        status.put("isSynced", Math.abs(diskUsage - dbTotalSize) < 1024 * 1024); // 1MB 이내면 동기화됨

        return status;
    }
}
