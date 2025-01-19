package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.domain.RealEstatePost;
import com.koreandubai.handubi.global.common.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DetailedRealEstatePost {

    private final Long id;

    private final String title;

    private final String body;

    private final String author;

    private final SubCategoryType subCategory;

    private final ProductType productType;

    private final PostStatus postStatus;

    private final Long view;

    private final String thumbnailUrl;

    private final ProductStatus productStatus;

    private final Long innerArea;

    private final Long totalArea;

    private final State state;

    private final Long price;

    private final Long like;

    private final LocalDateTime createdAt;


    @Builder
    public DetailedRealEstatePost(Long id, String title, String body, String author, SubCategoryType subCategory, ProductType productType, PostStatus postStatus, long view, String thumbnailUrl, Long innerArea, Long totalArea, State state, Long price, LocalDateTime createdAt, ProductStatus productStatus, Long like) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.subCategory = subCategory;
        this.productType = productType;
        this.postStatus = postStatus;
        this.view = view;
        this.thumbnailUrl = thumbnailUrl;
        this.innerArea = innerArea;
        this.totalArea = totalArea;
        this.state = state;
        this.price = price;
        this.createdAt = createdAt;
        this.productStatus = productStatus;
        this.like = like;
    }

    public static DetailedRealEstatePost toResponse(RealEstatePost post, String author, Long like) {
        return DetailedRealEstatePost.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .author(author)
                .subCategory(post.getSubCategory())
                .productType(post.getProductType())
                .postStatus(post.getPostStatus())
                .view(post.getView())
                .thumbnailUrl(post.getThumbnailUrl())
                .innerArea(post.getInnerArea())
                .totalArea(post.getTotalArea())
                .state(post.getState())
                .price(post.getPrice())
                .createdAt(post.getCreatedAt())
                .productStatus(post.getProductStatus())
                .like(like)
                .build();
    }

    public static List<DetailedRealEstatePost> toList(List<RealEstatePost> posts, List<String> names, List<Long> like) {

        List<DetailedRealEstatePost> detailedPosts = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            RealEstatePost post = posts.get(i);
            String name = names.get(i);
            Long likeCount = like.get(i);
            detailedPosts.add(DetailedRealEstatePost.toResponse(post, name, likeCount));
        }
        return detailedPosts;
    }
}

