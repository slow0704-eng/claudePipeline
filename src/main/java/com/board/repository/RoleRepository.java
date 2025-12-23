package com.board.repository;

import com.board.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // 역할명으로 조회
    Optional<Role> findByName(String name);

    // 역할명 존재 여부
    boolean existsByName(String name);

    // 활성화된 역할만 조회 (우선순위 순)
    List<Role> findByEnabledTrueOrderByPriorityAsc();

    // 모든 역할 조회 (우선순위 순)
    List<Role> findAllByOrderByPriorityAsc();

    // 우선순위 범위로 조회
    List<Role> findByPriorityBetweenOrderByPriorityAsc(Integer minPriority, Integer maxPriority);

    // 시스템 역할 조회
    List<Role> findByIsSystemTrue();
}
