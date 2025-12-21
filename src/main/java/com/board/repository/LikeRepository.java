package com.board.repository;

import com.board.entity.Like;
import com.board.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    long countByTargetTypeAndTargetId(TargetType targetType, Long targetId);
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    List<Like> findByUserIdAndTargetTypeOrderByCreatedAtDesc(Long userId, TargetType targetType);

    // 통계용 쿼리 메서드
    long countByCreatedAtAfter(LocalDateTime dateTime);
}
