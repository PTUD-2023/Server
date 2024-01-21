package com.example.insurance.exception;

import com.example.insurance.common.CustomErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("Insurance Server");

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleUnwantedException(Exception e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"UnknownError","An unknown error", new Date()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorResponse> handleBodyIsMissingException(HttpMessageNotReadableException e)
    {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), "BodyRequestNotFound","Missing body request",new Date()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(e.getErrorCode()).body(new CustomErrorResponse(e.getErrorCode(), e.getErrorKey(),e.getMessage(),new Date()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorResponse> handleSignInException(Exception e)
    {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(
                HttpStatus.BAD_REQUEST.value(),"EmailOrPasswordInValid","Email or password is incorrect",new Date()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomErrorResponse> handleDuplicateEmailSignUpException(DataIntegrityViolationException e)
    {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),"EmailAlreadyExist","Email Already Exist",new Date()));
    }
}
