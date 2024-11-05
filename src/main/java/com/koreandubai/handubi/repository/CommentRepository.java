package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    Optional<Comment> getCommentById(Long id);
}
