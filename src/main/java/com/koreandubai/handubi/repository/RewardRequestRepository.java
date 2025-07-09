package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.RewardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RewardRequestRepository extends JpaRepository<RewardRequest, Long> {
    List<RewardRequest> findAllByUserId(Long userId);

    @Query("SELECT r FROM RewardRequest r ORDER BY r.createdAt DESC")
    List<RewardRequest> findAllOrderByCreatedAtDesc();

    Optional<RewardRequest> findFirstByUserIdAndProductIdAndStatusOrderByCreatedAtDesc(
            Long userId, Long productId, RewardRequest.RequestStatus status);

}

