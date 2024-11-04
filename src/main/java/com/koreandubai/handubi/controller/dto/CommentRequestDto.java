package com.koreandubai.handubi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {

    @NotNull
    private Long postId;

    @NotBlank(message = "You should enter a content.")
    @Size(max = 50, min = 1)
    private String content;

    @NotBlank
    private Long preCommentId;

    @Builder
    public CommentRequestDto(Long postId, String content, Long preCommentId) {
        this.postId = postId;
        this.content = content;
        this.preCommentId = preCommentId;
    }
}
