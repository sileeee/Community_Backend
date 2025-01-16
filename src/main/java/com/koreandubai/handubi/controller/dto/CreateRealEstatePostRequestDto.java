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
public class CreateRealEstatePostRequestDto {

    @NotBlank(message = "You should enter a title.")
    @Size(max = 50, min = 1)
    private final String title;

    @NotBlank(message = "You should enter a content.")
    @Size(min = 1)
    private final String body;

    @NotNull(message = "You should choose sub category of post.")
    private final SubCategoryType subCategory;

    @NotNull(message = "You should choose productType of post.")
    private final ProductType productType;

    @NotNull(message = "You should choose postStatus of post.")
    private final PostStatus postStatus;

    @NotNull(message = "You should choose status of post.")
    private final String thumbnailUrl;

    @NotNull(message = "You should choose innerArea of post.")
    private final Long innerArea;

    @NotNull(message = "You should choose totalArea of post.")
    private final Long totalArea;

    @NotNull(message = "You should choose state of post.")
    private final String state;

    @NotNull(message = "You should choose price of post.")
    private final Long price;

    @NotNull(message = "You should choose productStatus of post.")
    private final ProductStatus productStatus;


    @Builder
    public CreateRealEstatePostRequestDto(String title, String body, SubCategoryType subCategory, ProductType productType, PostStatus postStatus, String thumbnailUrl, Long innerArea, Long totalArea, String state, Long price, ProductStatus productStatus) {
        this.title = title;
        this.body = body;
        this.subCategory = subCategory;
        this.productType = productType;
        this.postStatus = postStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.innerArea = innerArea;
        this.totalArea = totalArea;
        this.state = state;
        this.price = price;
        this.productStatus = productStatus;
    }
}
