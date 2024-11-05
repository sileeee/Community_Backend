package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.global.common.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByCategory(CategoryType categoryType, Pageable pageable);

    Optional<Post> getPostsById(Long postId);

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.body) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}