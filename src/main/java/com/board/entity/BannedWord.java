package com.board.entity;

import com.board.enums.BannedWordAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString
@Table(name = "banned_words",
       indexes = {
           @Index(name = "idx_banned_word_enabled", columnList = "enabled")
       })
public class BannedWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String word;

    @Column(length = 500)
    private String description;

    @Column(name = "is_regex", nullable = false)
    private Boolean isRegex = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BannedWordAction action = BannedWordAction.BLOCK;

    @Column(nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
