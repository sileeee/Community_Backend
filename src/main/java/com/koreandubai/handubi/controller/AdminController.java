package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.ProductRegisterDto;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.global.util.auth.AuthRequired;
import com.koreandubai.handubi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@AuthRequired
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/point/list")
    public SuccessResponse list(@RequestParam(required = false) String query) {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(adminService.pointList(query))
                .message("Admin point list")
                .build();
    }

    @GetMapping("/reward/list")
    public SuccessResponse getRewardList(@RequestParam(required = false) String query) {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(adminService.getRewardRequestList(query))
                .message("Reward successfully accepted")
                .build();
    }

    @PostMapping("/reward/{requestId}/accept")
    public SuccessResponse acceptReward(@PathVariable("requestId") Long requestId) {

        adminService.rewardAccept(requestId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Reward successfully accepted")
                .build();
    }

    @PostMapping("/reward/{requestId}/reject")
    public SuccessResponse rejectReward(@PathVariable("requestId") Long requestId) {

        adminService.rewardDecline(requestId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Reward successfully rejected")
                .build();
    }

    @PostMapping("/product/register")
    public SuccessResponse registerEvent(@RequestBody ProductRegisterDto dto) {
        adminService.registerEvent(dto.getName(), dto.getDescription(), dto.getPointPrice(), dto.getStockQty(), dto.getDeadline());

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Event successfully registered")
                .build();
    }

    @PostMapping("/product/{productId}/end")
    public SuccessResponse endEvent(@PathVariable("productId") Long productId) {
        adminService.deactivateEvent(productId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Event successfully ended")
                .build();
    }

    @GetMapping("/product/active")
    public SuccessResponse getActiveEvents() {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(adminService.getActiveEvents())
                .message("Active events fetched")
                .build();
    }

    @PostMapping("/product/{productId}/start")
    public SuccessResponse activateProduct(@PathVariable("productId") Long productId) {
        adminService.activateProduct(productId);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully activated product")
                .build();
    }


    @GetMapping("/product/list")
    public SuccessResponse getAllProducts() {
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(adminService.getAllProducts())
                .message("All products fetched")
                .build();
    }
}
