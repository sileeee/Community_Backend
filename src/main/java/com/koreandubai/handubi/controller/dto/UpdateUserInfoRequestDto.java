package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserInfoRequestDto {

    private final String email;

    private final String name;

    private final String password;

    private final String phone;


    @Builder
    public UpdateUserInfoRequestDto(String email, String name, String password, String phone) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }
}