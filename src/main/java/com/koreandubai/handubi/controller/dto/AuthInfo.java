package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthInfo {

    private final Long userId;

    private final String name;

    private final boolean isLoggedIn;

    private final UserType role;

    @Builder
    public AuthInfo(Long userId, String name, boolean isLoggedIn, UserType role) {
        this.userId = userId;
        this.name = name;
        this.isLoggedIn = isLoggedIn;
        this.role = role;
    }
}
