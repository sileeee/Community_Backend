package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class DetailedPost {

    private String title;

    private String body;

    private String author;

    private long view;

    private LocalDateTime createdAt;


    @Builder
    public DetailedPost(String title, String body, String author, long view, LocalDateTime createdAt) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.view = view;
        this.createdAt = createdAt;
    }
}
