package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.PostType;
import com.koreandubai.handubi.global.common.SubCategoryType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;


@Getter
public class CreatePostRequestDto {

    @NotBlank(message = "You should enter a title.")
    @Size(max = 50, min = 1)
    private final String title;

    @NotBlank(message = "You should enter a content.")
    @Size(min = 1)
    private final String body;

    @Column(name = "thumbnail_url")
    private final String thumbnailUrl;

    @NotNull(message = "You should choose sub category of post.")
    private final SubCategoryType subCategory;

    @NotNull(message = "You should choose status of post.")
    private final PostStatus postStatus;

    private final PostType postType;


    @Builder
    public CreatePostRequestDto(String title, String body, String thumbnailUrl, SubCategoryType subCategory, PostStatus postStatus, PostType postType) {
        this.title = title;
        this.body = body;
        this.thumbnailUrl = thumbnailUrl;
        this.subCategory = subCategory;
        this.postStatus = postStatus;
        this.postType = postType;
    }
}
