package com.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 역할-메뉴 권한 매핑 엔티티
 * 어떤 역할이 어떤 메뉴에 접근 가능한지 정의
 */
@Entity
@Table(name = "role_menu_permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "menu_id"}),
       indexes = {
           @Index(name = "idx_rmp_role", columnList = "role_id"),
           @Index(name = "idx_rmp_menu", columnList = "menu_id")
       })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class RoleMenuPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 역할 ID
     */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 메뉴 ID
     */
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    /**
     * 읽기 권한 (조회)
     */
    @Column(name = "can_read", nullable = false)
    private Boolean canRead = true;

    /**
     * 쓰기 권한 (생성, 수정)
     */
    @Column(name = "can_write", nullable = false)
    private Boolean canWrite = false;

    /**
     * 삭제 권한
     */
    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
