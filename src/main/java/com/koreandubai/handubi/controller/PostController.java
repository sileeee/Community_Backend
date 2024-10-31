package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.DetailedPost;
import com.koreandubai.handubi.controller.dto.EditPostRequestDto;
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

    @GetMapping("/{id}")
    public SuccessResponse getSinglePost(@PathVariable("id") long postId) {

        DetailedPost post = postService.getSinglePost(postId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully get a single post")
                .data(post)
                .build();
    }

    @PostMapping("/new/{category}")
    public SuccessResponse createPost(HttpServletRequest request,
                                      @PathVariable(value = "category")CategoryType categoryType,
                                      @Valid @RequestBody CreatePostRequestDto requestDto) {

        postService.createPost(request, categoryType, requestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully create posts")
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public SuccessResponse deletePost(@PathVariable("id") long postId, HttpServletRequest request) {

        postService.deletePost(request, postId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully delete posts")
                .build();
    }

    @PutMapping("/edit/{id}")
    public SuccessResponse editPost(HttpServletRequest request,
                                    @PathVariable("id") long postId,
                                    @Valid @RequestBody EditPostRequestDto requestDto) {

        postService.editPost(request, postId, requestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully edit posts")
                .build();
    }
}