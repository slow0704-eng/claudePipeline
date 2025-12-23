package com.board.repository;

import com.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 이름으로 카테고리 조회
     */
    Optional<Category> findByName(String name);

    /**
     * 활성화된 카테고리 목록 조회 (정렬 순서대로)
     */
    List<Category> findByEnabledTrueOrderByDisplayOrderAsc();

    /**
     * 모든 카테고리 목록 조회 (정렬 순서대로)
     */
    List<Category> findAllByOrderByDisplayOrderAsc();

    /**
     * 특정 이름의 카테고리가 존재하는지 확인
     */
    boolean existsByName(String name);
}
