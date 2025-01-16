package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.*;
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
}, name = "`real_estate_post`")
public class RealEstatePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "sub_category")
    private SubCategoryType subCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status")
    private ProductStatus productStatus;

    @Column(name = "inner_area")
    private Long innerArea;

    @Column(name = "total_area")
    private Long totalArea;

    @Column(length = 50)
    private String state;

    private Long price;

    @Builder
    public RealEstatePost(Long id, SubCategoryType subCategory, ProductType productType, String title, String body, Long userId, PostStatus postStatus, Long view, String thumbnailUrl, LocalDateTime lastModified, ProductStatus productStatus, long innerArea, long totalArea, String state, long price) {
        this.id = id;
        this.subCategory = subCategory;
        this.productType = productType;
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.postStatus = postStatus;
        this.view = view;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
        this.lastModified = lastModified;
        this.productStatus = productStatus;
        this.innerArea = innerArea;
        this.totalArea = totalArea;
        this.state = state;
        this.price = price;
    }

    public RealEstatePost() {

    }
}