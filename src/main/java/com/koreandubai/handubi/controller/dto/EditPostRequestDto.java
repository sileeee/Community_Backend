package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;


@Getter
public class EditPostRequestDto {

    @NotBlank(message = "You should enter a title.")
    @Size(min = 1, max = 50)
    private final String title;

    @Size(min = 1)
    @NotBlank(message = "You should enter a content.")
    private final String body;

    @NotNull(message = "You should enter a sub category.")
    private final SubCategoryType subCategory;

    @NotNull(message = "You should choose status of post.")
    private final PostStatus status;


    @Builder
    public EditPostRequestDto(String title, String body, SubCategoryType subCategory, PostStatus status) {
        this.title = title;
        this.body = body;
        this.subCategory = subCategory;
        this.status = status;
    }
}
