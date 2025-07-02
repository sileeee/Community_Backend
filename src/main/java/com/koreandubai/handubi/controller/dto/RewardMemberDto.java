package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RewardMemberDto {

    private final Long id;
    private Long userId;
    private String name;
    private String email;
    private Integer totalPoints;
    private final Long productId;
    private Integer pointsUsed;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

