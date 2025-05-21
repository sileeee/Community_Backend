package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.CategoryType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AdInfo {

    private final CategoryType category;
    private final String title;
    private final String imageUrl;
    private final String linkUrl;

    @Builder
    public AdInfo(CategoryType category, String title, String imageUrl, String linkUrl) {
        this.category = category;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }
}
