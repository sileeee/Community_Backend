package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.CategoryType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "home")
public class Home {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "post_id")
    private Long postId;

    @Column(nullable = false, name = "location_id")
    private Long locationId;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;

    @Builder
    public Home(Long postId, Long locationId, String imageUrl, CategoryType categoryType) {
        this.postId = postId;
        this.locationId = locationId;
        this.imageUrl = imageUrl;
        this.categoryType = categoryType;
    }

    public Home() {
    }
}
