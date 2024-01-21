package com.example.insurance.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpResponse {
    private String accessToken;
    private String refreshToken;
    private String message;
    private String key;
    public SignUpResponse(String accessToken, String refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
