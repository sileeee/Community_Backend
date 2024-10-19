package com.koreandubai.handubi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInRequestDto {

    @NotBlank(message = "You should enter a email.")
    @Email(message = "You should ensure email format.")
    private String email;

    @NotBlank(message = "You should enter a password.")
    private String password;

    @Builder
    public SignInRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
