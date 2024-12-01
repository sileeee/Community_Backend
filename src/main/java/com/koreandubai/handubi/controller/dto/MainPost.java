package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.SubCategoryType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MainPost {

    @NotNull
    private final Long postId;

    @NotNull
    private final String title;

    @NotNull
    private final String content;

    @NotNull
    private final Long locationId;

    private final String imageUrl;

    private final CategoryType categoryType;

    private final SubCategoryType subCategoryType;

    @Builder
    public MainPost(Long postId, String title, String content, Long locationId, String imageUrl, CategoryType categoryType, SubCategoryType subCategoryType) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.locationId = locationId;
        this.imageUrl = imageUrl;
        this.categoryType = categoryType;
        this.subCategoryType = subCategoryType;
    }
}
