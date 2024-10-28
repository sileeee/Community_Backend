package com.koreandubai.handubi.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EncryptedPassword {

    private String salt;
    private String password;

    @Builder
    public EncryptedPassword(String salt, String password) {
        this.salt = salt;
        this.password = password;
    }
}
