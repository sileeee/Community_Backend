package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.AdInfo;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;


    @GetMapping("/banners")
    public SuccessResponse getAdBanners(@RequestParam(value = "category", required = false) CategoryType category) {

        List<AdInfo> banners = adService.getActiveAdBanners(category);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(banners)
                .message("Successfully get main posts")
                .build();
    }
}