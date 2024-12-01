package com.koreandubai.handubi.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SecureUserInfo {

    private final Long id;

    private final String name;

    private final String email;

    private final String phone;

    private final String birthday;

    @Builder
    public SecureUserInfo(Long id, String name, String email, String phone, String birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
    }
}
