package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.RealEstatePost;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RealEstateRepository extends JpaRepository<RealEstatePost, Long> {

    Page<RealEstatePost> findAllByPostStatus(PostStatus postStatus, Pageable pageable);

    Page<RealEstatePost> findAllBySubCategoryAndPostStatus(SubCategoryType subCategory, PostStatus postStatus, Pageable pageable);

    Optional<RealEstatePost> getPostsById(Long postId);

    @Query(value = "SELECT * FROM real_estate_post WHERE LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR (body IS NOT NULL AND LOWER(body) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    Page<RealEstatePost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
