package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.global.common.SubCategoryType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
public class SimplePost {
    @NotNull
    private long id;

    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    SubCategoryType subCategory;

    @NotNull
    private LocalDateTime createdAt;

    @Builder
    public SimplePost(long id, String title, String author, SubCategoryType subCategory, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.subCategory = subCategory;
        this.createdAt = createdAt;
    }

    public static SimplePost toResponse(Post post, String author) {
        return SimplePost.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(author)
                .subCategory(post.getSubCategory())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static List<SimplePost> toList(List<Post> posts, List<String> names) {

        List<SimplePost> simplePosts = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            String name = names.get(i);
            simplePosts.add(SimplePost.toResponse(post, name));
        }
        return simplePosts;
    }
}
