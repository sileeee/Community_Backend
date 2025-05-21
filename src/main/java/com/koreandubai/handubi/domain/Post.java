package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import com.koreandubai.handubi.global.common.PostType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_body", columnList = "body")
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubCategoryType subCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "post_status")
    private PostStatus postStatus;

    private Long view;

    @Column(name = "thumbnail_url", length = 2048)
    private String thumbnailUrl;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Builder
    public Post(Long id, CategoryType category, SubCategoryType subCategory, String title, String body, Long userId, PostStatus postStatus, PostType postType, Long view, String thumbnailUrl, LocalDateTime lastModified) {
        this.id = id;
        this.category = category;
        this.subCategory = subCategory;
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.postStatus = postStatus;
        this.postType = postType;
        this.view = view;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
        this.lastModified = lastModified;
    }

    public Post() {

    }
}