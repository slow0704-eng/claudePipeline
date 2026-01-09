package com.board.dto.response;

import com.board.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파일 업로드 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private Long id;
    private Long boardId;
    private String originalFileName;
    private String storedFilePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    /**
     * Entity -> Response DTO 변환
     */
    public static FileUploadResponse from(Attachment attachment) {
        return FileUploadResponse.builder()
                .id(attachment.getId())
                .boardId(attachment.getBoardId())
                .originalFileName(attachment.getOriginalFileName())
                .storedFilePath(attachment.getStoredFilePath())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}
