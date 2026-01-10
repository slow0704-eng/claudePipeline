package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 게시글-해시태그 중간 테이블
 * - 게시글과 해시태그의 다대다 관계 관리
 */
@Entity
@Table(name = "board_hashtag",
       indexes = {
           @Index(name = "idx_bh_board_id", columnList = "board_id"),
           @Index(name = "idx_bh_hashtag_id", columnList = "hashtag_id")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "hashtag_id"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class BoardHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 ID
     */
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    /**
     * 해시태그 ID
     */
    @Column(name = "hashtag_id", nullable = false)
    private Long hashtagId;

    /**
     * 관계 생성 시간
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
