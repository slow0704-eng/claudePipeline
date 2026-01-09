package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 게시글-주제 매핑 엔티티
 * 게시글과 주제의 다대다 관계를 관리합니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"board", "topic"})
@Table(name = "board_topics", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"board_id", "topic_id"})
})
public class BoardTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", insertable = false, updatable = false)
    private Long boardId;

    /**
     * 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "topic_id", insertable = false, updatable = false)
    private Long topicId;

    /**
     * 주제
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 생성자

    public BoardTopic(Board board, Topic topic) {
        this.board = board;
        this.topic = topic;
        this.boardId = board.getId();
        this.topicId = topic.getId();
    }
}
