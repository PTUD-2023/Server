package com.example.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmCodeRequest {
    private String email;
    private String code;
}
