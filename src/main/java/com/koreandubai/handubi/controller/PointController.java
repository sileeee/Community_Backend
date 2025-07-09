package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.AddPointRequestDto;
import com.koreandubai.handubi.controller.dto.RewardRequestDto;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.global.util.auth.AuthRequired;
import com.koreandubai.handubi.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {

    private final PointService pointService;

    @PostMapping("/add")
    public SuccessResponse addPoint(@RequestBody AddPointRequestDto dto) {
        pointService.addPoint(dto);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Point successfully added")
                .build();
    }

    @PostMapping("/reward")
    public SuccessResponse requestReward(@RequestBody RewardRequestDto dto) {
        pointService.requestReward(dto);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Reward request successfully submitted")
                .build();
    }

    @AuthRequired
    @GetMapping("/{id}/history")
    public SuccessResponse getPointHistory(@PathVariable("id") Long userId) {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(pointService.getUserHistory(userId))
                .message("Successfully fetched point history")
                .build();
    }

    @AuthRequired
    @GetMapping("/{id}/rewards")
    public SuccessResponse getRewardRequests(@PathVariable("id") Long userId) {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(pointService.getUserRewards(userId))
                .message("Successfully fetched reward requests")
                .build();
    }

    @AuthRequired
    @PostMapping("/rewards/cancel")
    public SuccessResponse cancelRewardRequests(@RequestBody RewardRequestDto dto) {
        pointService.cancelUserRewards(dto);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully fetched reward requests")
                .build();
    }

    @AuthRequired
    @GetMapping("/{id}/total")
    public SuccessResponse getUserTotalPoints(@PathVariable("id") Long userId) {
        int points = pointService.getUserTotalPoints(userId);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully fetched total points")
                .data(points)
                .build();
    }

    @GetMapping("/product/active")
    public SuccessResponse getActiveEvents() {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(pointService.getActiveEvents())
                .message("Active events fetched")
                .build();
    }
}

