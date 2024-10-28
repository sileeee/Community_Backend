package com.koreandubai.handubi.global.exception;

public class NoAuthorizationData extends RuntimeException {

    private static final String message = "Insufficient user credentials.";

    public NoAuthorizationData(){
        super(message);
    }
}
