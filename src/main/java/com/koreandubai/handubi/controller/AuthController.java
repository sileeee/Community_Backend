package com.koreandubai.handubi.controller;

import com.koreandubai.handubi.controller.dto.AuthInfo;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;

    @GetMapping("/session")
    public SuccessResponse isLoggedIn(HttpServletRequest request) {

        AuthInfo authInfo = loginService.isLoggedIn(request);

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(authInfo)
                .message("Successfully get information from the session")
                .build();
    }
}
