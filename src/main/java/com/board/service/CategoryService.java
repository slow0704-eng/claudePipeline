package com.board.service;

import com.board.entity.Category;
import com.board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 모든 카테고리 조회 (정렬 순서대로)
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    /**
     * 활성화된 카테고리만 조회 (정렬 순서대로)
     */
    public List<Category> getEnabledCategories() {
        return categoryRepository.findByEnabledTrueOrderByDisplayOrderAsc();
    }

    /**
     * ID로 카테고리 조회
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
    }

    /**
     * 이름으로 카테고리 조회
     */
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElse(null);
    }

    /**
     * 카테고리 생성
     */
    @Transactional
    public Category createCategory(String name, String description, Integer displayOrder) {
        // 중복 체크
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        category.setEnabled(true);

        return categoryRepository.save(category);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public Category updateCategory(Long id, String name, String description, Integer displayOrder, Boolean enabled) {
        Category category = getCategoryById(id);

        // 이름 변경 시 중복 체크
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
        }

        category.setName(name);
        category.setDescription(description);
        if (displayOrder != null) {
            category.setDisplayOrder(displayOrder);
        }
        if (enabled != null) {
            category.setEnabled(enabled);
        }

        return categoryRepository.save(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    /**
     * 카테고리 활성화/비활성화
     */
    @Transactional
    public Category toggleCategoryStatus(Long id) {
        Category category = getCategoryById(id);
        category.setEnabled(!category.getEnabled());
        return categoryRepository.save(category);
    }

    /**
     * 카테고리 순서 변경
     */
    @Transactional
    public void updateDisplayOrder(Long id, Integer newOrder) {
        Category category = getCategoryById(id);
        category.setDisplayOrder(newOrder);
        categoryRepository.save(category);
    }

    /**
     * 기본 카테고리 초기화
     */
    @Transactional
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            createCategory("자유게시판", "자유롭게 이야기를 나누는 공간입니다.", 1);
            createCategory("공지사항", "중요한 공지사항을 확인하세요.", 2);
            createCategory("질문게시판", "궁금한 점을 질문하세요.", 3);
            createCategory("정보공유", "유용한 정보를 공유하세요.", 4);
        }
    }
}
