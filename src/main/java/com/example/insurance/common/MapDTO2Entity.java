package com.example.insurance.common;

import com.example.insurance.dto.NewClaimRequest;
import com.example.insurance.entity.ClaimRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapDTO2Entity {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MapDTO2Entity instance = new MapDTO2Entity();

    private MapDTO2Entity() {
        // private constructor thực thi signleton pattern
    }

    public static MapDTO2Entity getInstance() {
        return instance;
    }

    public ClaimRequest mapDTO2ClaimRequest(NewClaimRequest newClaimRequest) {
        return objectMapper.convertValue(newClaimRequest, ClaimRequest.class);
    }
}
