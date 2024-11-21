package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.LikeRequestDto;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public SuccessResponse toggleLike(HttpServletRequest request,
                                      @Valid @RequestBody LikeRequestDto likeRequestDto) {

        likeService.toggleLike(request, likeRequestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully toggle like")
                .build();
    }

    @GetMapping("/count/{id}")
    public SuccessResponse getLikeCount(@PathVariable("id") long postId) {

        long likeCount = likeService.getLikeCount(postId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully get like count")
                .data(likeCount)
                .build();
    }

    @GetMapping("/{id}")
    public SuccessResponse isUserToggled(HttpServletRequest request,
                                         @PathVariable("id") long postId) {

        boolean likeCount = likeService.isUserLiked(request, postId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully get user's like info")
                .data(likeCount)
                .build();
    }
}

