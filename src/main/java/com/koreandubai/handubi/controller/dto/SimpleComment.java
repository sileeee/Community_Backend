package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.domain.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Getter
public class SimpleComment {

    @NotNull
    private long id;

    @NotNull
    private String content;

    @NotNull
    private String author;

    private long preCommentId;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private boolean isDeleted;

    @Builder
    public SimpleComment(long id, String content, String author, long preCommentId, LocalDateTime createdAt, boolean isDeleted) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.preCommentId = preCommentId;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public static SimpleComment toResponse(Comment comment, String author) {
        return SimpleComment.builder()
                .id(comment.getId())
                .content(comment.getBody())
                .author(author)
                .createdAt(comment.getCreatedAt())
                .preCommentId(Optional.ofNullable(comment.getPreCommentId()).orElseGet(() -> 0L))
                .isDeleted(comment.isDeleted())
                .build();
    }

    public static List<SimpleComment> toList(List<Comment> comments, List<String> names) {

        List<SimpleComment> simpleComments = new ArrayList<>();

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            String name = names.get(i);
            simpleComments.add(SimpleComment.toResponse(comment, name));
        }
        return simpleComments;
    }
}
