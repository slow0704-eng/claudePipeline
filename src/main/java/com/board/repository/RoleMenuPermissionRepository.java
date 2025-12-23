package com.board.repository;

import com.board.entity.RoleMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleMenuPermissionRepository extends JpaRepository<RoleMenuPermission, Long> {

    // 특정 역할의 모든 권한 조회
    List<RoleMenuPermission> findByRoleId(Long roleId);

    // 특정 메뉴에 대한 모든 권한 조회
    List<RoleMenuPermission> findByMenuId(Long menuId);

    // 특정 역할-메뉴 권한 조회
    Optional<RoleMenuPermission> findByRoleIdAndMenuId(Long roleId, Long menuId);

    // 특정 역할의 읽기 가능한 메뉴 ID 목록
    @Query("SELECT rmp.menuId FROM RoleMenuPermission rmp WHERE rmp.roleId = :roleId AND rmp.canRead = true")
    List<Long> findReadableMenuIdsByRoleId(@Param("roleId") Long roleId);

    // 특정 역할의 쓰기 가능한 메뉴 ID 목록
    @Query("SELECT rmp.menuId FROM RoleMenuPermission rmp WHERE rmp.roleId = :roleId AND rmp.canWrite = true")
    List<Long> findWritableMenuIdsByRoleId(@Param("roleId") Long roleId);

    // 특정 역할의 삭제 가능한 메뉴 ID 목록
    @Query("SELECT rmp.menuId FROM RoleMenuPermission rmp WHERE rmp.roleId = :roleId AND rmp.canDelete = true")
    List<Long> findDeletableMenuIdsByRoleId(@Param("roleId") Long roleId);

    // 역할 ID로 모든 권한 삭제
    @Modifying
    @Query("DELETE FROM RoleMenuPermission rmp WHERE rmp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    // 메뉴 ID로 모든 권한 삭제
    @Modifying
    @Query("DELETE FROM RoleMenuPermission rmp WHERE rmp.menuId = :menuId")
    void deleteByMenuId(@Param("menuId") Long menuId);

    // 권한 존재 여부
    boolean existsByRoleIdAndMenuId(Long roleId, Long menuId);
}
