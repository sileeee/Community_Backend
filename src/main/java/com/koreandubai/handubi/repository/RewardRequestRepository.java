package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.RewardRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRequestRepository extends JpaRepository<RewardRequest, Long> {
    List<RewardRequest> findAllByUserId(Long userId);
}

