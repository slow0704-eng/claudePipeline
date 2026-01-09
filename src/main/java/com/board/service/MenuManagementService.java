package com.board.service;

import com.board.entity.Menu;
import com.board.entity.RoleMenuPermission;
import com.board.repository.MenuRepository;
import com.board.repository.RoleMenuPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 메뉴 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuManagementService {

    private final MenuRepository menuRepository;
    private final RoleMenuPermissionRepository permissionRepository;

    /**
     * 모든 메뉴 조회 (계층 구조 유지)
     */
    public List<Menu> getAllMenus() {
        return menuRepository.findAllOrderByHierarchy();
    }

    /**
     * 최상위 메뉴만 조회
     */
    public List<Menu> getTopLevelMenus() {
        return menuRepository.findByParentIdIsNullOrderByDisplayOrderAsc();
    }

    /**
     * 특정 부모의 하위 메뉴 조회
     */
    public List<Menu> getChildMenus(Long parentId) {
        return menuRepository.findByParentIdOrderByDisplayOrderAsc(parentId);
    }

    /**
     * 활성화된 메뉴만 조회 (계층 구조)
     */
    public List<Menu> getActiveMenus() {
        return menuRepository.findAll().stream()
                .filter(Menu::getEnabled)
                .sorted((m1, m2) -> {
                    int levelCompare = m1.getLevel().compareTo(m2.getLevel());
                    if (levelCompare != 0) return levelCompare;
                    return m1.getDisplayOrder().compareTo(m2.getDisplayOrder());
                })
                .collect(Collectors.toList());
    }

    /**
     * ID로 메뉴 조회
     */
    public Menu getMenuById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + id));
    }

    /**
     * 메뉴 생성
     */
    @Transactional
    public Menu createMenu(String name, String description, Long parentId, String url,
                           String icon, Integer displayOrder, String menuType) {
        Menu menu = Menu.builder().build();
        menu.setName(name);
        menu.setDescription(description);
        menu.setParentId(parentId);
        menu.setUrl(url);
        menu.setIcon(icon);
        menu.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        menu.setMenuType(menuType != null ? menuType : "MENU");
        menu.setEnabled(true);

        // 레벨 계산
        if (parentId == null) {
            menu.setLevel(0);
        } else {
            Menu parent = getMenuById(parentId);
            menu.setLevel(parent.getLevel() + 1);
        }

        return menuRepository.save(menu);
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public Menu updateMenu(Long id, String name, String description, Long parentId,
                          String url, String icon, Integer displayOrder, Boolean enabled) {
        Menu menu = getMenuById(id);

        menu.setName(name);
        menu.setDescription(description);
        menu.setUrl(url);
        menu.setIcon(icon);

        if (displayOrder != null) {
            menu.setDisplayOrder(displayOrder);
        }
        if (enabled != null) {
            menu.setEnabled(enabled);
        }

        // 부모 변경 시 레벨 재계산
        if (parentId != null && !parentId.equals(menu.getParentId())) {
            menu.setParentId(parentId);
            Menu parent = getMenuById(parentId);
            menu.setLevel(parent.getLevel() + 1);
        } else if (parentId == null && menu.getParentId() != null) {
            menu.setParentId(null);
            menu.setLevel(0);
        }

        return menuRepository.save(menu);
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    public void deleteMenu(Long id) {
        Menu menu = getMenuById(id);

        // 하위 메뉴가 있는지 확인
        List<Menu> children = menuRepository.findByParentIdOrderByDisplayOrderAsc(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("하위 메뉴가 있어 삭제할 수 없습니다. 먼저 하위 메뉴를 삭제해주세요.");
        }

        // 연관된 권한 먼저 삭제
        permissionRepository.deleteByMenuId(id);

        // 메뉴 삭제
        menuRepository.delete(menu);
    }

    /**
     * 메뉴 활성화/비활성화 토글
     */
    @Transactional
    public Menu toggleMenuStatus(Long id) {
        Menu menu = getMenuById(id);
        menu.setEnabled(!menu.getEnabled());
        return menuRepository.save(menu);
    }

    /**
     * 메뉴 순서 변경
     */
    @Transactional
    public Menu updateMenuOrder(Long id, Integer newOrder) {
        Menu menu = getMenuById(id);
        menu.setDisplayOrder(newOrder);
        return menuRepository.save(menu);
    }

    /**
     * 계층 구조로 메뉴 트리 구성
     */
    public List<Map<String, Object>> getMenuTree() {
        List<Menu> allMenus = getAllMenus();
        return buildMenuTree(allMenus, null);
    }

    /**
     * 재귀적으로 메뉴 트리 구성
     */
    private List<Map<String, Object>> buildMenuTree(List<Menu> allMenus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();

        for (Menu menu : allMenus) {
            boolean isMatch = (parentId == null && menu.getParentId() == null) ||
                             (parentId != null && parentId.equals(menu.getParentId()));

            if (isMatch) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", menu.getId());
                node.put("name", menu.getName());
                node.put("description", menu.getDescription());
                node.put("url", menu.getUrl());
                node.put("icon", menu.getIcon());
                node.put("level", menu.getLevel());
                node.put("displayOrder", menu.getDisplayOrder());
                node.put("enabled", menu.getEnabled());
                node.put("menuType", menu.getMenuType());
                node.put("parentId", menu.getParentId());

                // 하위 메뉴 재귀 조회
                List<Map<String, Object>> children = buildMenuTree(allMenus, menu.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }

        return tree;
    }

    /**
     * 특정 역할이 접근 가능한 메뉴 조회
     */
    public List<Menu> getAccessibleMenus(Long roleId) {
        List<Long> accessibleMenuIds = permissionRepository.findReadableMenuIdsByRoleId(roleId);
        if (accessibleMenuIds.isEmpty()) {
            return new ArrayList<>();
        }
        return menuRepository.findAllById(accessibleMenuIds);
    }

    /**
     * 특정 역할의 메뉴 트리 (권한 기반)
     */
    public List<Map<String, Object>> getAccessibleMenuTree(Long roleId) {
        List<Menu> accessibleMenus = getAccessibleMenus(roleId);
        return buildMenuTree(accessibleMenus, null);
    }

    /**
     * 기본 메뉴 초기화
     */
    @Transactional
    public void initializeDefaultMenus() {
        if (menuRepository.count() == 0) {
            // 대시보드
            Menu dashboard = createMenu("대시보드", "시스템 현황 및 통계", null, "/admin", "📊", 1, "MENU");

            // 사용자 관리
            Menu userMgmt = createMenu("사용자 관리", "회원 관리", null, "/admin/users", "👥", 2, "MENU");

            // 게시글 관리
            Menu boardMgmt = createMenu("게시글 관리", "게시글 관리", null, "/admin/boards", "📝", 3, "MENU");

            // 카테고리 관리
            Menu categoryMgmt = createMenu("카테고리 관리", "카테고리 관리", null, "/admin/categories", "🏷️", 4, "MENU");

            // 금지어 관리
            Menu bannedWordMgmt = createMenu("금지어 관리", "금지어 관리", null, "/admin/banned-words", "🚫", 5, "MENU");

            // 파일 관리
            Menu fileMgmt = createMenu("파일 관리", "파일 스토리지 관리", null, "/admin/files", "📁", 6, "MENU");

            // 시스템 설정 (최상위)
            Menu systemSettings = createMenu("시스템 설정", "시스템 환경 설정", null, null, "⚙️", 7, "MENU");

            // 시스템 설정 > 역할 관리
            createMenu("역할 관리", "역할 및 권한 관리", systemSettings.getId(), "/admin/roles", "🔑", 1, "MENU");

            // 시스템 설정 > 메뉴 관리
            createMenu("메뉴 관리", "메뉴 구조 관리", systemSettings.getId(), "/admin/menus", "📋", 2, "MENU");

            // 시스템 설정 > 권한 매핑
            createMenu("권한 매핑", "역할별 메뉴 권한 설정", systemSettings.getId(), "/admin/role-permissions", "🔐", 3, "MENU");

            // 게시판 (일반 사용자용)
            Menu board = createMenu("게시판", "게시판 보기", null, "/board", "🏠", 100, "MENU");

            // 마이페이지
            Menu mypage = createMenu("마이페이지", "내 정보", null, "/mypage", "👤", 101, "MENU");

            log.info("Default menus initialized successfully");
        }
    }
}
