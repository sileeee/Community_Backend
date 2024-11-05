package com.koreandubai.handubi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class EditCommentRequestDto {

    @NotBlank(message = "You should enter a content.")
    @Size(max = 50, min = 1)
    private String content;


    @Builder
    public EditCommentRequestDto(String content) {
        this.content = content;
    }
}
