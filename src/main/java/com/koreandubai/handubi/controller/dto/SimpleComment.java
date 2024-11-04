package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.domain.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class SimpleComment {

    @NotNull
    private long id;

    @NotNull
    private String content;

    @NotNull
    private String author;

    @NotNull
    private LocalDateTime createdAt;

    @Builder
    public SimpleComment(long id, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    public static SimpleComment toResponse(Comment comment, String author) {
        return SimpleComment.builder()
                .id(comment.getId())
                .content(comment.getBody())
                .author(author)
                .createdAt(comment.getCreatedAt())
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
