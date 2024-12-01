package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdvertisementPost {

    private final long id;

    private final String imageUrl;

    @Builder
    public AdvertisementPost(long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}
