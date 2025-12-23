package com.board.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 파일 스토리지 정책 설정
 */
@Configuration
@ConfigurationProperties(prefix = "file.storage")
@Getter
@Setter
public class FileStorageConfig {

    /**
     * 업로드 디렉토리
     */
    private String uploadDir = "uploads";

    /**
     * 최대 파일 크기 (바이트)
     * 기본값: 50MB
     */
    private Long maxFileSize = 50 * 1024 * 1024L;

    /**
     * 최대 파일 크기 (MB, 표시용)
     */
    private Integer maxFileSizeMb = 50;

    /**
     * 게시글당 최대 파일 개수
     * 기본값: 5개
     */
    private Integer maxFilesPerBoard = 5;

    /**
     * 허용되는 이미지 확장자
     */
    private List<String> imageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    /**
     * 허용되는 문서 확장자
     */
    private List<String> documentExtensions = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "hwp");

    /**
     * 허용되는 압축 파일 확장자
     */
    private List<String> archiveExtensions = Arrays.asList("zip", "rar", "7z", "tar", "gz");

    /**
     * 허용되는 동영상 확장자
     */
    private List<String> videoExtensions = Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm");

    /**
     * 자동 정리 활성화 여부
     */
    private Boolean autoCleanupEnabled = true;

    /**
     * 자동 정리 주기 (cron 표현식)
     * 기본값: 매일 새벽 3시
     */
    private String autoCleanupCron = "0 0 3 * * ?";

    /**
     * 자동 정리 기준 일수
     * 기본값: 30일
     */
    private Integer autoCleanupDays = 30;

    /**
     * 삭제 전 백업 활성화 여부
     */
    private Boolean backupBeforeDelete = true;

    /**
     * 썸네일 생성 여부
     */
    private Boolean thumbnailEnabled = true;

    /**
     * 썸네일 너비
     */
    private Integer thumbnailWidth = 200;

    /**
     * 썸네일 높이
     */
    private Integer thumbnailHeight = 200;

    /**
     * 모든 허용된 확장자 목록 반환
     */
    public List<String> getAllAllowedExtensions() {
        List<String> all = new java.util.ArrayList<>();
        all.addAll(imageExtensions);
        all.addAll(documentExtensions);
        all.addAll(archiveExtensions);
        all.addAll(videoExtensions);
        return all;
    }

    /**
     * 파일 확장자 허용 여부 확인
     */
    public boolean isAllowedExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String ext = extension.toLowerCase();
        return getAllAllowedExtensions().contains(ext);
    }

    /**
     * 파일 크기 허용 여부 확인
     */
    public boolean isAllowedFileSize(long size) {
        return size <= maxFileSize;
    }
}
