package com.koreandubai.handubi.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "post_id")
    private Long postId;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(name = "pre_comment_id")
    private Long preCommentId;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder
    public Comment(Long id, Long postId, String body, Long userId, Long preCommentId, LocalDateTime createdAt, LocalDateTime lastModified, boolean isDeleted) {
        this.id = id;
        this.postId = postId;
        this.body = body;
        this.userId = userId;
        this.preCommentId = preCommentId;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.isDeleted = isDeleted;
    }

    public Comment() {

    }
}
