package com.board.service;

import com.board.entity.Attachment;
import com.board.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final AttachmentRepository attachmentRepository;

    // 최대 파일 크기: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 허용되는 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    // 최대 첨부파일 개수
    private static final int MAX_FILES_PER_BOARD = 5;

    // 썸네일 크기
    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 파일 업로드
     */
    @Transactional
    public Attachment uploadFile(MultipartFile file, Long boardId) throws IOException {
        // 파일 검증
        validateFile(file);

        // 게시글당 최대 파일 개수 검증
        validateMaxFilesPerBoard(boardId);

        // 업로드 디렉토리 생성
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(uploadDir, dateFolder);
        Files.createDirectories(uploadPath);

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedFileName = UUID.randomUUID().toString() + "." + extension;
        String storedFilePath = dateFolder + "/" + storedFileName;

        // 파일 저장
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 이미지인 경우 썸네일 생성
        if (isImageFile(extension)) {
            try {
                createThumbnail(filePath.toString(), uploadPath.toString(), storedFileName);
            } catch (Exception e) {
                log.error("Failed to create thumbnail: {}", e.getMessage());
                // 썸네일 생성 실패해도 파일 업로드는 계속 진행
            }
        }

        // DB에 저장
        Attachment attachment = new Attachment();
        attachment.setBoardId(boardId);
        attachment.setOriginalFileName(originalFilename);
        attachment.setStoredFilePath(storedFilePath);
        attachment.setFileType(extension);
        attachment.setFileSize(file.getSize());

        return attachmentRepository.save(attachment);
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("파일명이 올바르지 않습니다.");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("허용되지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 가능)");
        }
    }

    /**
     * 게시글당 최대 파일 개수 검증
     */
    private void validateMaxFilesPerBoard(Long boardId) {
        long currentCount = attachmentRepository.countByBoardId(boardId);
        if (currentCount >= MAX_FILES_PER_BOARD) {
            throw new RuntimeException("게시글당 최대 " + MAX_FILES_PER_BOARD + "개의 파일만 업로드할 수 있습니다.");
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 이미지 파일 여부 확인
     */
    private boolean isImageFile(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 썸네일 생성
     */
    private void createThumbnail(String originalPath, String thumbnailDir, String fileName) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(originalPath));
        if (originalImage == null) {
            throw new IOException("이미지를 읽을 수 없습니다.");
        }

        // 썸네일 크기 계산 (비율 유지)
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newWidth = THUMBNAIL_WIDTH;
        int newHeight = THUMBNAIL_HEIGHT;

        if (originalWidth > originalHeight) {
            newHeight = (int) ((double) originalHeight / originalWidth * THUMBNAIL_WIDTH);
        } else {
            newWidth = (int) ((double) originalWidth / originalHeight * THUMBNAIL_HEIGHT);
        }

        // 썸네일 이미지 생성
        BufferedImage thumbnailImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnailImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // 썸네일 저장
        String extension = getFileExtension(fileName);
        String thumbnailFileName = "thumb_" + fileName;
        File thumbnailFile = new File(thumbnailDir, thumbnailFileName);
        ImageIO.write(thumbnailImage, extension, thumbnailFile);
    }

    /**
     * 첨부파일 조회
     */
    public List<Attachment> getAttachmentsByBoardId(Long boardId) {
        return attachmentRepository.findByBoardId(boardId);
    }

    /**
     * 첨부파일 삭제
     */
    @Transactional
    public void deleteFile(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("첨부파일을 찾을 수 없습니다."));

        // 물리적 파일 삭제
        try {
            Path filePath = Paths.get(uploadDir, attachment.getStoredFilePath());
            Files.deleteIfExists(filePath);

            // 썸네일 삭제
            String thumbnailPath = filePath.getParent().toString() + "/thumb_" + filePath.getFileName().toString();
            Files.deleteIfExists(Paths.get(thumbnailPath));
        } catch (IOException e) {
            log.error("Failed to delete file: {}", e.getMessage());
        }

        // DB에서 삭제
        attachmentRepository.delete(attachment);
    }

    /**
     * 게시글의 모든 첨부파일 삭제
     */
    @Transactional
    public void deleteFilesByBoardId(Long boardId) {
        List<Attachment> attachments = attachmentRepository.findByBoardId(boardId);
        for (Attachment attachment : attachments) {
            deleteFile(attachment.getId());
        }
    }

    /**
     * 업로드 디렉토리 초기화
     */
    public void initUploadDirectory() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Upload directory created: {}", uploadDir);
            }
        } catch (IOException e) {
            log.error("Failed to create upload directory: {}", e.getMessage());
            throw new RuntimeException("업로드 디렉토리 생성에 실패했습니다.");
        }
    }
}
