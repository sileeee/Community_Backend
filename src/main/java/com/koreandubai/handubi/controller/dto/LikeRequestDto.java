package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.LikeType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequestDto {

    @NotNull
    private Long postId;

    @NotNull
    private LikeType likeType;
}
