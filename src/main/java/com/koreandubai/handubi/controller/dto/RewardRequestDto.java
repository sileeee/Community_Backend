package com.koreandubai.handubi.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardRequestDto {

    private Long userId;

    private String rewardType;

    private Integer pointsUsed;
}

