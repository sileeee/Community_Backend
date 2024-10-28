package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.SimplePost;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;


    @GetMapping
    public SuccessResponse getPosts(@RequestParam(required = false, value = "category")CategoryType categoryType,
                                    @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
                                    @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria) {

        List<SimplePost> products = postService.getPosts(categoryType, pageNo, criteria);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully get list of posts")
                .data(products)
                .build();
    }

    @PostMapping
    public SuccessResponse createPost(HttpServletRequest request,
                                      @RequestParam(required = false, value = "category")CategoryType categoryType,
                                      @Valid @RequestBody CreatePostRequestDto requestDto) {

        postService.createPost(request, categoryType, requestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully create posts")
                .build();
    }
}