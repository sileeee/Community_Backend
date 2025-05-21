package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.AdStatus;
import com.koreandubai.handubi.global.common.CategoryType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "link_url", length = 2048)
    private String linkUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Builder
    public Advertisement(long id, CategoryType category, String title, String imageUrl, String linkUrl, AdStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Advertisement() {

    }
}