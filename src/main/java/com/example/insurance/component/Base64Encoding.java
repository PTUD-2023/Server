package com.example.insurance.component;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Encoding {
    public static String encodeStringToBase64(String input) {
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
        return new String(encodedBytes);
    }

    public static String decodeBase64ToString(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
