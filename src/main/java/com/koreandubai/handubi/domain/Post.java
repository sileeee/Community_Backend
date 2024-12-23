package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
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

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    private Long view;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Builder
    public Post(Long id, CategoryType category, SubCategoryType subCategory, String title, String body, Long userId, PostStatus status, Long view, LocalDateTime lastModified) {
        this.id = id;
        this.category = category;
        this.subCategory = subCategory;
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.status = status;
        this.view = view;
        this.createdAt = LocalDateTime.now();
        this.lastModified = lastModified;
    }

    public Post() {

    }
}