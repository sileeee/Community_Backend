package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Getter
public class GetUploadedImage {

    private final MediaType contentType;
    private final Resource body;

    @Builder
    public GetUploadedImage(MediaType contentType, Resource body) {
        this.contentType = contentType;
        this.body = body;
    }
}
