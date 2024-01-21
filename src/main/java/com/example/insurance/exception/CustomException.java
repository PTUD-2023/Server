package com.example.insurance.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private int errorCode;
    private String errorKey;
    public CustomException(int errorCode,String errorKey ,String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorKey = errorKey;
    }
}
