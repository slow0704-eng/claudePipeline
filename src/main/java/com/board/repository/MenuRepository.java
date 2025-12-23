package com.board.repository;

import com.board.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    // 최상위 메뉴 조회 (부모 없음)
    List<Menu> findByParentIdIsNullOrderByDisplayOrderAsc();

    // 특정 부모의 하위 메뉴 조회
    List<Menu> findByParentIdOrderByDisplayOrderAsc(Long parentId);

    // 활성화된 최상위 메뉴
    List<Menu> findByParentIdIsNullAndEnabledTrueOrderByDisplayOrderAsc();

    // 활성화된 하위 메뉴
    List<Menu> findByParentIdAndEnabledTrueOrderByDisplayOrderAsc(Long parentId);

    // 레벨별 메뉴 조회
    List<Menu> findByLevelOrderByDisplayOrderAsc(Integer level);

    // 모든 메뉴 조회 (계층 구조 유지)
    @Query("SELECT m FROM Menu m ORDER BY m.level ASC, m.displayOrder ASC")
    List<Menu> findAllOrderByHierarchy();

    // URL로 메뉴 조회
    List<Menu> findByUrl(String url);

    // 메뉴와 그 하위 메뉴 모두 조회 (재귀 쿼리 대신 서비스 레벨에서 처리)
    @Query("SELECT m FROM Menu m WHERE m.id = :menuId OR m.parentId = :menuId ORDER BY m.displayOrder ASC")
    List<Menu> findMenuWithChildren(@Param("menuId") Long menuId);
}
