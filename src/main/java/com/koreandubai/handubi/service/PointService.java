package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.AddPointRequestDto;
import com.koreandubai.handubi.controller.dto.RewardRequestDto;
import com.koreandubai.handubi.domain.PointHistory;
import com.koreandubai.handubi.domain.RewardRequest;
import com.koreandubai.handubi.domain.UserPoint;
import com.koreandubai.handubi.repository.PointHistoryRepository;
import com.koreandubai.handubi.repository.RewardRequestRepository;
import com.koreandubai.handubi.repository.UserPointRepository;
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

    @Transactional
    public void addPoint(AddPointRequestDto dto) {

        UserPoint userPoint = userPointRepository.findByUserId(dto.getUserId())
                .orElse(UserPoint.builder()
                        .userId(dto.getUserId())
                        .totalPoints(0)
                        .build());

        userPoint.setTotalPoints(userPoint.getTotalPoints() + dto.getPoints());
        userPoint.setUpdatedAt(LocalDateTime.now());
        userPointRepository.save(userPoint);

        PointHistory history = PointHistory.builder()
                .userId(dto.getUserId())
                .actionType(dto.getActionType())
                .points(dto.getPoints())
                .referencePostId(dto.getReferencePostId())
                .referenceCommentId(dto.getReferenceCommentId())
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
                .rewardType(dto.getRewardType())
                .pointsUsed(dto.getPointsUsed())
                .status(RewardRequest.RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        rewardRequestRepository.save(reward);
    }

    public List<PointHistory> getUserHistory(Long userId) {
        return pointHistoryRepository.findAllByUserId(userId);
    }

    public List<RewardRequest> getUserRewards(Long userId) {
        return rewardRequestRepository.findAllByUserId(userId);
    }
}

