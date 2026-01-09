package com.board.dto.response;

/**
 * 파일 업로드 응답 DTO
 */
public class FileUploadResponse {

    /**
     * 파일명 (원본 파일명)
     */
    private String fileName;

    /**
     * 저장된 파일명 (서버에 저장된 파일명)
     */
    private String storedFileName;

    /**
     * 파일 URL (다운로드 또는 접근 URL)
     */
    private String fileUrl;

    /**
     * 파일 크기 (bytes)
     */
    private Long fileSize;

    /**
     * 파일 타입 (MIME type)
     */
    private String fileType;

    /**
     * 기본 생성자
     */
    public FileUploadResponse() {
    }

    /**
     * 파일 업로드 응답 생성
     *
     * @param fileName 원본 파일명
     * @param storedFileName 저장된 파일명
     * @param fileUrl 파일 URL
     * @param fileSize 파일 크기
     * @param fileType 파일 타입
     */
    public FileUploadResponse(String fileName, String storedFileName, String fileUrl, Long fileSize, String fileType) {
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    // Getters and Setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Attachment 엔티티로부터 FileUploadResponse 생성
     */
    public static FileUploadResponse from(com.board.entity.Attachment attachment) {
        return new FileUploadResponse(
            attachment.getOriginalFileName(),
            attachment.getStoredFilePath(),
            "/files/" + attachment.getId(),  // URL 생성
            attachment.getFileSize(),
            attachment.getFileType()
        );
    }
}
