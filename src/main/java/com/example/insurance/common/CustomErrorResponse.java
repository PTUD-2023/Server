package com.example.insurance.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CustomErrorResponse {
    private int statusCode;
    private String errorKey;
    private String message;
    private Date date;
}
