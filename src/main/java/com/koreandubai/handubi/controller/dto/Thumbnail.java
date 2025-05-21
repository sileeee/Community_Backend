package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Thumbnail {

    private final long id;

    private final String imageUrl;

    @Builder
    public Thumbnail(long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
