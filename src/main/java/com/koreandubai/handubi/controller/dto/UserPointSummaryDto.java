package com.koreandubai.handubi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserPointSummaryDto {
    private Long userId;
    private String email;
    private String name;
    private Integer totalPoints;
    private LocalDateTime updatedAt;
}

