package com.koreandubai.handubi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopLikedPostDto {
    private Long postId;
    private Long likeCount;
}
