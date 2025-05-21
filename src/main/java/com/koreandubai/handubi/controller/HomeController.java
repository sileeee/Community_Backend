package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.Thumbnail;
import com.koreandubai.handubi.controller.dto.EditMainPostDto;
import com.koreandubai.handubi.controller.dto.MainPost;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;


    @GetMapping("/posts")
    public SuccessResponse getMainPosts() {

        List<MainPost> posts = homeService.getMainPosts();

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(posts)
                .message("Successfully get main posts")
                .build();
    }


    @PostMapping("/edit/posts")
    public SuccessResponse setMainPosts(EditMainPostDto editMainPostDtos) {

        homeService.editMainPosts(editMainPostDtos);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully set main posts")
                .build();
    }

    @GetMapping("/posts/{id}")
    public SuccessResponse getThumbnail(@PathVariable("id") final long id) {

        List<Thumbnail> posts = homeService.getThumbnail(id);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(posts)
                .message("Successfully get main posts")
                .build();
    }
}
