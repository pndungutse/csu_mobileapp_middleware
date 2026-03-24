package com.dsu.hope_bank_app_middleware.exception;

import org.springframework.http.HttpStatus;

public class CustomAPIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public CustomAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public CustomAPIException(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
