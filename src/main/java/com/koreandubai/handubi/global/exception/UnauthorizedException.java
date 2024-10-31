package com.koreandubai.handubi.global.exception;

public class UnauthorizedException extends RuntimeException {

    private static final String message = "Unauthorized user.";

    public UnauthorizedException(){
        super(message);
    }

    public UnauthorizedException(Exception e){
        super(message, e);
    }

    public UnauthorizedException(String message){
        super(message);
    }
}
