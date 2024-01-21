package com.example.insurance.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomSuccessResponse {
    private String message;
    private String key;

    public CustomSuccessResponse(String message)
    {
        this.message = message;
    }
}
