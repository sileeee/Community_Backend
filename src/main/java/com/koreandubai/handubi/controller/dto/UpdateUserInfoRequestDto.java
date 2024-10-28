package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserInfoRequestDto {

    private final String name;

    private final String password;

    private final String phone;


    @Builder
    public UpdateUserInfoRequestDto(String name, String password, String phone) {
        this.name = name;
        this.password = password;
        this.phone = phone;
    }
}