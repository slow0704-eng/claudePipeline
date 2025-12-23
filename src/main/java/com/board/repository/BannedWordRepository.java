package com.board.repository;

import com.board.entity.BannedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannedWordRepository extends JpaRepository<BannedWord, Long> {

    // 활성화된 금지어 목록 조회
    List<BannedWord> findByEnabledTrueOrderByCreatedAtDesc();

    // 모든 금지어 목록 조회 (활성화 여부 무관)
    List<BannedWord> findAllByOrderByCreatedAtDesc();

    // 특정 단어 존재 여부 확인
    boolean existsByWordAndEnabled(String word, Boolean enabled);
}
