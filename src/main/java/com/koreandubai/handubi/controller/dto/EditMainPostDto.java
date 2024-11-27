package com.koreandubai.handubi.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EditMainPostDto {

    @NotNull
    private final Long postId;

    private final String imageUrl;

    private final Long locationId;

    public EditMainPostDto(Long postId, String imageUrl, Long locationId) {
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.locationId = locationId;
    }
}
