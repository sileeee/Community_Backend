package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class DetailedPost {

    private Long id;

    private String title;

    private String body;

    private String author;

    private SubCategoryType subCategory;

    private CategoryType category;

    private PostStatus status;

    private long view;

    private LocalDateTime createdAt;


    @Builder
    public DetailedPost(Long id, String title, String body, String author, CategoryType category, SubCategoryType subCategory, PostStatus status, long view, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.category = category;
        this.status = status;
        this.subCategory = subCategory;
        this.view = view;
        this.createdAt = createdAt;
    }
}
