package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.UserType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthInfo {

    @NotNull
    private final boolean isLoggedIn;

    private final UserType role;

    @Builder
    public AuthInfo(boolean isLoggedIn, UserType role) {
        this.isLoggedIn = isLoggedIn;
        this.role = role;
    }
}
