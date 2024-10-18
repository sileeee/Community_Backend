package com.koreandubai.handubi.controller;


import com.koreandubai.handubi.controller.dto.SignUpRequestDto;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/sign-up")
    public SuccessResponse signUp(@Valid @RequestBody final SignUpRequestDto requestDto) {

        userService.join(requestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.CREATED)
                .message("Successfully registered")
                .build();
    }
}
