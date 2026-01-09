package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 역할(Role) 엔티티
 * 사용자 그룹을 정의하고 관리
 */
@Entity
@Table(name = "roles",
       uniqueConstraints = @UniqueConstraint(columnNames = "name"),
       indexes = {
           @Index(name = "idx_role_enabled", columnList = "enabled"),
           @Index(name = "idx_role_priority", columnList = "priority")
       })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 역할명 (고유값)
     * 예: ADMIN, MANAGER, EDITOR, MEMBER
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 역할 표시명 (한글 등)
     * 예: 관리자, 매니저, 에디터, 일반회원
     */
    @Column(nullable = false, length = 100)
    private String displayName;

    /**
     * 역할 설명
     */
    @Column(length = 500)
    private String description;

    /**
     * 우선순위 (숫자가 낮을수록 높은 권한)
     * 1 = 최고 관리자, 10 = 일반 회원
     */
    @Column(nullable = false)
    private Integer priority = 10;

    /**
     * 활성화 여부
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 시스템 기본 역할 여부
     * true인 경우 삭제 불가
     */
    @Column(nullable = false)
    private Boolean isSystem = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
