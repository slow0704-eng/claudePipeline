package com.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 메뉴 엔티티
 * 시스템의 기능 메뉴를 정의하고 계층 구조 지원
 */
@Entity
@Table(name = "menus",
       indexes = {
           @Index(name = "idx_menu_parent", columnList = "parent_id"),
           @Index(name = "idx_menu_order", columnList = "display_order"),
           @Index(name = "idx_menu_enabled", columnList = "enabled")
       })
@Data
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 메뉴명
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 메뉴 설명
     */
    @Column(length = 500)
    private String description;

    /**
     * 부모 메뉴 ID (계층 구조)
     * null이면 최상위 메뉴
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 메뉴 URL
     */
    @Column(length = 200)
    private String url;

    /**
     * 아이콘 클래스 또는 이모지
     * 예: "📊", "fa-dashboard" 등
     */
    @Column(length = 50)
    private String icon;

    /**
     * 표시 순서 (같은 레벨 내에서)
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    /**
     * 메뉴 레벨 (0 = 최상위, 1 = 1단계 하위 등)
     */
    @Column(nullable = false)
    private Integer level = 0;

    /**
     * 활성화 여부
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 메뉴 타입
     * MENU = 일반 메뉴, BUTTON = 버튼, DIVIDER = 구분선
     */
    @Column(length = 20)
    private String menuType = "MENU";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
