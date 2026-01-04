package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments",
       indexes = @Index(name = "idx_attachment_board", columnList = "board_id"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "board_id")
    private Long boardId;

    @Column(nullable = false, name = "original_file_name")
    private String originalFileName;

    @Column(nullable = false, length = 500, name = "stored_file_path")
    private String storedFilePath;

    @Column(nullable = false, length = 100, name = "file_type")
    private String fileType;

    @Column(nullable = false, name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(updatable = false, name = "uploaded_at")
    private LocalDateTime uploadedAt;
}
