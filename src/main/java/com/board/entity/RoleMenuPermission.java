package com.board.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public void setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMenuPermission that = (RoleMenuPermission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoleMenuPermission{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", menuId=" + menuId +
                ", canRead=" + canRead +
                ", canWrite=" + canWrite +
                ", canDelete=" + canDelete +
                ", createdAt=" + createdAt +
                '}';
    }
}
