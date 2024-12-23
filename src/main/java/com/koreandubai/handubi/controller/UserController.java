package com.koreandubai.handubi.controller;


import com.koreandubai.handubi.controller.dto.UpdateUserInfoRequestDto;
import com.koreandubai.handubi.controller.dto.SignInRequestDto;
import com.koreandubai.handubi.controller.dto.SignUpRequestDto;
import com.koreandubai.handubi.global.common.StatusEnum;
import com.koreandubai.handubi.global.common.SuccessResponse;
import com.koreandubai.handubi.global.util.auth.AuthRequired;
import com.koreandubai.handubi.service.LoginService;
import com.koreandubai.handubi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    public SuccessResponse signUp(@Valid @RequestBody final SignUpRequestDto requestDto) {

        userService.join(requestDto);

        return SuccessResponse.builder()
                .status(StatusEnum.CREATED)
                .message("Successfully registered")
                .build();
    }

    @PostMapping("/login")
    public SuccessResponse signIn(@Valid @RequestBody SignInRequestDto requestDto) {

        loginService.login(requestDto);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully login")
                .build();
    }

    @GetMapping("/logout")
    public void logoutUser(){

        loginService.logout();
    }

    @AuthRequired
    @PutMapping("/update/{id}")
    public SuccessResponse updateMember(@PathVariable("id") final long id,
                                        @Valid @RequestBody UpdateUserInfoRequestDto requestDto){
        userService.updateUserInfo(id, requestDto);
        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Successfully update user information")
                .build();
    }

    @AuthRequired
    @GetMapping("/{id}")
    public SuccessResponse getSecureUserInfo(@PathVariable("id") final long id){

        return SuccessResponse.builder()
                .status(StatusEnum.OK)
                .data(userService.getSecureUserInfo(id))
                .message("Successfully update user information")
                .build();
    }
}
