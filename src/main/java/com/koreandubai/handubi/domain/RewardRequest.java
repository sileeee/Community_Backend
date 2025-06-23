package com.koreandubai.handubi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward_request")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(name = "points_used")
    private Integer pointsUsed;

    @Column(name = "reward_type")
    private String rewardType;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    @Builder
    public RewardRequest(Long userId, Integer pointsUsed, String rewardType, RequestStatus status) {
        this.userId = userId;
        this.pointsUsed = pointsUsed;
        this.rewardType = rewardType;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

