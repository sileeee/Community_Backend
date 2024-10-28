package com.koreandubai.handubi.global.exception;

import com.koreandubai.handubi.global.common.FailResponse;
import com.koreandubai.handubi.global.common.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public FailResponse handleIllegalArgumentException(IllegalArgumentException e){

        return FailResponse.builder()
                .status(StatusEnum.BAD_REQUEST)
                .errorMessage(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public FailResponse handleUnathorizedException(UnauthorizedException e){

        return FailResponse.builder()
                .status(StatusEnum.UNAUTHORIZED)
                .errorMessage(e.getMessage())
                .build();
    }
}