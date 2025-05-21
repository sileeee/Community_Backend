package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Like;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    @Query("SELECT l.postId, COUNT(l.id) AS likeCount FROM Like l " +
            "WHERE l.postId IN :postIds AND l.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY l.postId " +
            "ORDER BY likeCount DESC")
    List<Object[]> findTopLikedPosts(@Param("postIds") List<Long> postIds,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
