package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.CommentRequestDto;
import com.koreandubai.handubi.controller.dto.EditCommentRequestDto;
import com.koreandubai.handubi.controller.dto.SimpleComment;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    public SuccessResponse getAllComments(@RequestParam(required = false, value = "post_id") Long postId,
                                          @RequestParam(required = false, defaultValue = "0", value = "page") int page,
                                          @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria) {

        List<SimpleComment> comments = commentService.getAllComments(postId, page, criteria);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully get list of comments")
                .data(comments)
                .build();
    }

    @PostMapping
    public SuccessResponse createComment(HttpServletRequest request, @Valid @RequestBody CommentRequestDto commentDto) {
        commentService.createComment(request, commentDto);

        return SuccessResponse.builder()
                .status(StatusEnum.CREATED)
                .message("Successfully create comment")
                .build();
    }

    @PutMapping("/{id}")
    public SuccessResponse updateComment(HttpServletRequest request,
                                         @PathVariable("id") Long commentId,
                                         @Valid @RequestBody EditCommentRequestDto requestDto) {

        commentService.updateComment(request, commentId, requestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully edit comment")
                .build();
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteComment(HttpServletRequest request, @PathVariable("id") Long commentId) {

        commentService.deleteComment(request, commentId);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully delete comment")
                .build();
    }
}

