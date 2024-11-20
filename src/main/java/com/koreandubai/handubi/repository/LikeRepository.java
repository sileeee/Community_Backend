package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);
}
