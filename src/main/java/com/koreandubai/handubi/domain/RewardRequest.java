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

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum RequestStatus { PENDING, APPROVED, REJECTED, SHIPPED, CANCELLED, COMPLETE }

    @Builder
    public RewardRequest(Long userId, Integer pointsUsed, RequestStatus status) {
        this.userId = userId;
        this.pointsUsed = pointsUsed;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

