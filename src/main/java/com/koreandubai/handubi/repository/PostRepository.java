package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.PostType;
import com.koreandubai.handubi.global.common.SubCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByCategoryAndPostStatus(
            CategoryType categoryType,
            PostStatus postStatus,
            Pageable pageable
    );

    Page<Post> findAllByCategoryAndSubCategoryAndPostStatus(
            CategoryType categoryType,
            SubCategoryType subCategory,
            PostStatus postStatus,
            Pageable pageable
    );

    Page<Post> findAllByCategoryAndSubCategoryAndPostStatusAndPostType(
            CategoryType categoryType,
            SubCategoryType subCategory,
            PostStatus postStatus,
            PostType postType,
            Pageable pageable
    );

    Page<Post> findAllByCategoryAndPostStatusAndPostType(
            CategoryType categoryType,
            PostStatus postStatus,
            PostType postType,
            Pageable pageable
    );

    Optional<Post> getPostsById(Long postId);

    @Query(value = "SELECT * FROM post WHERE LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR (body IS NOT NULL AND LOWER(body) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p.id FROM Post p WHERE p.category = :categoryType")
    List<Long> findPostIdsByCategory(@Param("categoryType") CategoryType categoryType);
}
