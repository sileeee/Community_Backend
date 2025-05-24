package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
public class DetailedPost {

    private Long id;

    private String title;

    private String body;

    private String author;

    private SubCategoryType subCategory;

    private CategoryType category;

    private PostStatus postStatus;

    private long view;

    private Long like;

    private LocalDateTime createdAt;

    private String thumbnailUrl;


    @Builder
    public DetailedPost(Long id, String title, String body, String author, CategoryType category, SubCategoryType subCategory, PostStatus postStatus, long view, Long like, LocalDateTime createdAt, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.category = category;
        this.postStatus = postStatus;
        this.subCategory = subCategory;
        this.view = view;
        this.like = like;
        this.createdAt = createdAt;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static DetailedPost toResponse(Post post, String author, Long like) {
        return DetailedPost.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .author(author)
                .category(post.getCategory())
                .subCategory(post.getSubCategory())
                .view(post.getView())
                .like(like)
                .postStatus(post.getPostStatus())
                .createdAt(post.getCreatedAt())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }

    public static List<DetailedPost> toList(List<Post> posts, List<String> names, List<Long> like) {

        List<DetailedPost> detailedPosts = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            String name = names.get(i);
            Long likeCount = like.get(i);
            detailedPosts.add(DetailedPost.toResponse(post, name, likeCount));
        }
        return detailedPosts;
    }
}
