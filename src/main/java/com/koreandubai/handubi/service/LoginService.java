package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.AuthInfo;
import com.koreandubai.handubi.controller.dto.SignInRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface LoginService {

    void login(SignInRequestDto dto);

    void logout();

    AuthInfo isLoggedIn(HttpServletRequest request);
}
