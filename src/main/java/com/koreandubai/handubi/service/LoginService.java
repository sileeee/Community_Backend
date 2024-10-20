package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.SignInRequestDto;

public interface LoginService {

    void login(SignInRequestDto dto);

    void logout();
}
