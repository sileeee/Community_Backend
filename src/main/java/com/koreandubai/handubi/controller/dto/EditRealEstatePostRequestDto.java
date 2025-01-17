package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.ProductStatus;
import com.koreandubai.handubi.global.common.ProductType;
import com.koreandubai.handubi.global.common.SubCategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EditRealEstatePostRequestDto {

    @NotBlank(message = "You should enter a title.")
    @Size(min = 1, max = 50)
    private final String title;

    @Size(min = 1)
    @NotBlank(message = "You should enter a content.")
    private final String body;

    @NotNull(message = "You should enter a sub category.")
    private final SubCategoryType subCategory;

    @NotNull(message = "You should choose productType of post.")
    private final ProductType productType;

    @NotNull(message = "You should choose postStatus of post.")
    private final PostStatus postStatus;

    private final String thumbnailUrl;

    private final Long innerArea;

    private final Long totalArea;

    private final String state;

    private final Long price;

    private final ProductStatus productStatus;



    @Builder
    public EditRealEstatePostRequestDto(String title, String body, SubCategoryType subCategory, PostStatus postStatus, String thumbnailUrl, Long innerArea, Long totalArea, String state, Long price, ProductStatus productStatus, ProductType productType) {
        this.title = title;
        this.body = body;
        this.subCategory = subCategory;
        this.postStatus = postStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.innerArea = innerArea;
        this.totalArea = totalArea;
        this.state = state;
        this.price = price;
        this.productStatus = productStatus;
        this.productType = productType;
    }
}
