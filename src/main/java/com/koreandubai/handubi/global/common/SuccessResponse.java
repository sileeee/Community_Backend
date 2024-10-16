package com.koreandubai.handubi.global.common;

import lombok.Builder;


public class SuccessResponse {

    private StatusEnum status;
    private String message;
    private Object data;

    @Builder
    public SuccessResponse(StatusEnum status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
