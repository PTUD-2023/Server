package com.example.insurance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(value = {"createdAt","updatedAt","status"})
public class InsuranceBenefitDTO {
    private long id;
    private String benefitName;
    private int minAge;
    private int maxAge;
    private int price;
    private int maxPayout;
    private int perTimesPayout;
}
