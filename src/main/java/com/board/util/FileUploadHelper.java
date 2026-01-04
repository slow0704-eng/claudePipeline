package com.board.util;

import com.board.exception.BusinessException;
import com.board.exception.ErrorCode;
import com.board.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 업로드 중복 로직을 처리하는 헬퍼 클래스
 *
 * 사용처:
 * - BoardController.create()
 * - BoardController.update()
 * - BoardController.saveDraft()
 * - BoardController.updateDraft()
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadHelper {

    private final FileUploadService fileUploadService;

    /**
     * 여러 파일을 업로드합니다.
     *
     * @param files 업로드할 파일 목록
     * @param boardId 게시글 ID
     * @throws BusinessException 파일 업로드 실패 시
     */
    public void handleFileUploads(List<MultipartFile> files, Long boardId) {
        if (files == null || files.isEmpty()) {
            log.debug("No files to upload for board: {}", boardId);
            return;
        }

        int uploadedCount = 0;
        int failedCount = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                log.debug("Skipping empty file");
                continue;
            }

            try {
                fileUploadService.uploadFile(file, boardId);
                uploadedCount++;
                log.debug("File uploaded successfully: {} (board: {})",
                         file.getOriginalFilename(), boardId);
            } catch (Exception e) {
                failedCount++;
                log.error("File upload failed: {} (board: {}), reason: {}",
                         file.getOriginalFilename(), boardId, e.getMessage());
                throw new BusinessException(
                    ErrorCode.FILE_UPLOAD_FAILED,
                    String.format("파일 업로드 실패: %s", file.getOriginalFilename())
                );
            }
        }

        log.info("File upload completed for board: {}. Success: {}, Failed: {}",
                 boardId, uploadedCount, failedCount);
    }

    /**
     * 단일 파일을 업로드합니다.
     *
     * @param file 업로드할 파일
     * @param boardId 게시글 ID
     * @throws BusinessException 파일 업로드 실패 시
     */
    public void handleSingleFileUpload(MultipartFile file, Long boardId) {
        if (file == null || file.isEmpty()) {
            log.debug("No file to upload for board: {}", boardId);
            return;
        }

        try {
            fileUploadService.uploadFile(file, boardId);
            log.info("Single file uploaded successfully: {} (board: {})",
                     file.getOriginalFilename(), boardId);
        } catch (Exception e) {
            log.error("Single file upload failed: {} (board: {}), reason: {}",
                     file.getOriginalFilename(), boardId, e.getMessage());
            throw new BusinessException(
                ErrorCode.FILE_UPLOAD_FAILED,
                String.format("파일 업로드 실패: %s", file.getOriginalFilename())
            );
        }
    }
}
