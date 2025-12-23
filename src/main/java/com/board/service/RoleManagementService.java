package com.board.service;

import com.board.entity.Role;
import com.board.entity.RoleMenuPermission;
import com.board.repository.RoleMenuPermissionRepository;
import com.board.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 역할 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleManagementService {

    private final RoleRepository roleRepository;
    private final RoleMenuPermissionRepository permissionRepository;

    /**
     * 모든 역할 조회 (우선순위 순)
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAllByOrderByPriorityAsc();
    }

    /**
     * 활성화된 역할만 조회
     */
    public List<Role> getActiveRoles() {
        return roleRepository.findByEnabledTrueOrderByPriorityAsc();
    }

    /**
     * ID로 역할 조회
     */
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + id));
    }

    /**
     * 이름으로 역할 조회
     */
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + name));
    }

    /**
     * 역할 생성
     */
    @Transactional
    public Role createRole(String name, String displayName, String description, Integer priority, Boolean isSystem) {
        // 중복 체크
        if (roleRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 역할명입니다: " + name);
        }

        Role role = new Role();
        role.setName(name);
        role.setDisplayName(displayName);
        role.setDescription(description);
        role.setPriority(priority != null ? priority : 10);
        role.setEnabled(true);
        role.setIsSystem(isSystem != null ? isSystem : false);

        return roleRepository.save(role);
    }

    /**
     * 역할 수정
     */
    @Transactional
    public Role updateRole(Long id, String name, String displayName, String description, Integer priority, Boolean enabled) {
        Role role = getRoleById(id);

        // 시스템 역할의 이름은 변경 불가
        if (role.getIsSystem() && !role.getName().equals(name)) {
            throw new RuntimeException("시스템 역할의 이름은 변경할 수 없습니다.");
        }

        // 이름 변경 시 중복 체크
        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 역할명입니다: " + name);
        }

        role.setName(name);
        role.setDisplayName(displayName);
        role.setDescription(description);
        if (priority != null) {
            role.setPriority(priority);
        }
        if (enabled != null) {
            role.setEnabled(enabled);
        }

        return roleRepository.save(role);
    }

    /**
     * 역할 삭제
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);

        // 시스템 역할은 삭제 불가
        if (role.getIsSystem()) {
            throw new RuntimeException("시스템 역할은 삭제할 수 없습니다.");
        }

        // 연관된 권한 먼저 삭제
        permissionRepository.deleteByRoleId(id);

        // 역할 삭제
        roleRepository.delete(role);
    }

    /**
     * 역할 활성화/비활성화 토글
     */
    @Transactional
    public Role toggleRoleStatus(Long id) {
        Role role = getRoleById(id);
        role.setEnabled(!role.getEnabled());
        return roleRepository.save(role);
    }

    /**
     * 역할 우선순위 변경
     */
    @Transactional
    public Role updateRolePriority(Long id, Integer newPriority) {
        Role role = getRoleById(id);
        role.setPriority(newPriority);
        return roleRepository.save(role);
    }

    /**
     * 역할별 메뉴 권한 조회
     */
    public List<RoleMenuPermission> getRolePermissions(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    /**
     * 역할에 메뉴 권한 부여
     */
    @Transactional
    public RoleMenuPermission grantMenuPermission(Long roleId, Long menuId, Boolean canRead, Boolean canWrite, Boolean canDelete) {
        // 기존 권한 확인
        RoleMenuPermission permission = permissionRepository
                .findByRoleIdAndMenuId(roleId, menuId)
                .orElse(new RoleMenuPermission());

        permission.setRoleId(roleId);
        permission.setMenuId(menuId);
        permission.setCanRead(canRead != null ? canRead : true);
        permission.setCanWrite(canWrite != null ? canWrite : false);
        permission.setCanDelete(canDelete != null ? canDelete : false);

        return permissionRepository.save(permission);
    }

    /**
     * 역할의 메뉴 권한 제거
     */
    @Transactional
    public void revokeMenuPermission(Long roleId, Long menuId) {
        permissionRepository.findByRoleIdAndMenuId(roleId, menuId)
                .ifPresent(permissionRepository::delete);
    }

    /**
     * 역할의 모든 메뉴 권한 일괄 설정
     */
    @Transactional
    public Map<String, Object> bulkUpdatePermissions(Long roleId, List<Map<String, Object>> permissions) {
        // 기존 권한 모두 삭제
        permissionRepository.deleteByRoleId(roleId);

        int savedCount = 0;
        for (Map<String, Object> perm : permissions) {
            Long menuId = Long.parseLong(perm.get("menuId").toString());
            Boolean canRead = (Boolean) perm.getOrDefault("canRead", true);
            Boolean canWrite = (Boolean) perm.getOrDefault("canWrite", false);
            Boolean canDelete = (Boolean) perm.getOrDefault("canDelete", false);

            grantMenuPermission(roleId, menuId, canRead, canWrite, canDelete);
            savedCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("roleId", roleId);
        result.put("savedCount", savedCount);
        return result;
    }

    /**
     * 기본 역할 초기화
     */
    @Transactional
    public void initializeDefaultRoles() {
        if (roleRepository.count() == 0) {
            // 최고 관리자
            createRole("SUPER_ADMIN", "최고 관리자", "시스템 모든 권한", 1, true);

            // 관리자
            createRole("ADMIN", "관리자", "관리 기능 접근 가능", 5, true);

            // 매니저
            createRole("MANAGER", "매니저", "콘텐츠 관리 가능", 7, false);

            // 에디터
            createRole("EDITOR", "에디터", "게시글 작성 및 수정 가능", 8, false);

            // 일반 회원
            createRole("MEMBER", "일반 회원", "기본 기능 사용", 10, true);

            log.info("Default roles initialized successfully");
        }
    }
}
