package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.global.common.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByCategory(CategoryType categoryType, Pageable pageable);

    Optional<Post> getPostsById(Long postId);
}