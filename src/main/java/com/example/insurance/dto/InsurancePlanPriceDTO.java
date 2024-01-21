package com.example.insurance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(value = {"effectiveDate","status","insurancePlan"})
public class InsurancePlanPriceDTO {

    private Long id;
    private int minAge;

    private int maxAge;

    private int price;

    private float rate;
}
