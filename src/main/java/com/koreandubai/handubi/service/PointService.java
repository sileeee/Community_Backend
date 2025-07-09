package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.AddPointRequestDto;
import com.koreandubai.handubi.controller.dto.RewardRequestDto;
import com.koreandubai.handubi.domain.*;
import com.koreandubai.handubi.global.common.ActionCode;
import com.koreandubai.handubi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final RewardRequestRepository rewardRequestRepository;
    private final ActionTypeRepository actionTypeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addPoint(AddPointRequestDto dto) {

        ActionType actionType = actionTypeRepository.findById(dto.getActionTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid actionTypeId"));

        int delta = (dto.getPoints() != null) ? dto.getPoints()
                : actionType.getDefaultPoint();

        UserPoint userPoint = userPointRepository.findByUserId(dto.getUserId())
                .orElse(UserPoint.builder()
                        .userId(dto.getUserId())
                        .totalPoints(0)
                        .build());

        userPoint.setTotalPoints(userPoint.getTotalPoints() + delta);
        userPoint.setUpdatedAt(LocalDateTime.now());
        userPointRepository.save(userPoint);

        PointHistory history = PointHistory.builder()
                .userId(dto.getUserId())
                .actionType(actionType)
                .points(delta)
                .referencePostId(dto.getReferencePostId())
                .referenceCommentId(dto.getReferenceCommentId())
                .referenceNote(dto.getReferenceNote())
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history);
    }

    @Transactional
    public void requestReward(RewardRequestDto dto) {

        UserPoint userPoint = userPointRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User point not found"));

        if (userPoint.getTotalPoints() < dto.getPointsUsed()) {
            throw new RuntimeException("Not enough points");
        }

        userPoint.setTotalPoints(userPoint.getTotalPoints() - dto.getPointsUsed());
        userPoint.setUpdatedAt(LocalDateTime.now());
        userPointRepository.save(userPoint);

        RewardRequest reward = RewardRequest.builder()
                .userId(dto.getUserId())
                .productId(dto.getProductId())
                .pointsUsed(dto.getPointsUsed())
                .status(RewardRequest.RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        rewardRequestRepository.save(reward);

        ActionType redeemType = actionTypeRepository.findByActionCode(ActionCode.REWARD_REDEEM)
                .orElseThrow(() -> new IllegalStateException("REWARD_REDEEM actionType missing"));

        pointHistoryRepository.save(PointHistory.builder()
                .userId(dto.getUserId())
                .actionType(redeemType)
                .points(-dto.getPointsUsed())
                .referenceNote("RewardRequest#" + reward.getId())
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void cancelUserRewards(RewardRequestDto dto) {
        RewardRequest req = rewardRequestRepository
                .findFirstByUserIdAndProductIdAndStatusOrderByCreatedAtDesc(
                        dto.getUserId(),
                        dto.getProductId(),
                        RewardRequest.RequestStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("No Pending reward request found"));

        UserPoint up = userPointRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new IllegalStateException("UserPoint not found"));
        up.setTotalPoints(up.getTotalPoints() + req.getPointsUsed());
        up.setUpdatedAt(LocalDateTime.now());

        productRepository.findById(req.getProductId())
                .ifPresent(p -> {
                    p.setStockQty(p.getStockQty() + 1);
                });

        req.setStatus(RewardRequest.RequestStatus.CANCELLED);
        req.setUpdatedAt(LocalDateTime.now());

        ActionType redeemType = actionTypeRepository
                .findByActionCode(ActionCode.REWARD_REDEEM)
                .orElseThrow(() -> new IllegalStateException("No REDEEM actionType missing"));

        pointHistoryRepository.save(PointHistory.builder()
                .userId(dto.getUserId())
                .actionType(redeemType)
                .points(req.getPointsUsed())
                .referenceNote("Reward Cancel #" + req.getId())
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<PointHistory> getUserHistory(Long userId) {
        return pointHistoryRepository.findAllByUserId(userId);
    }

    public List<RewardRequest> getUserRewards(Long userId) {
        return rewardRequestRepository.findAllByUserId(userId);
    }

    public Integer getUserTotalPoints(Long userId) {
        return userPointRepository.findByUserId(userId)
                .map(UserPoint::getTotalPoints)
                .orElse(0);
    }

    public List<Product> getActiveEvents() {
        LocalDateTime now = LocalDateTime.now();
        return productRepository.findByIsActiveTrueAndDeadlineIsNullOrIsActiveTrueAndDeadlineAfter(now);
    }

}

