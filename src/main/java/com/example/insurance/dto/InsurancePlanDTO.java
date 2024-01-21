package com.example.insurance.dto;

import com.example.insurance.entity.InsurancePlanPrice;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(value = {"createdAt","updatedAt","status"})
public class InsurancePlanDTO {
    private Long id;
    private String planName;
    private String duration;
    private int accidentInsurance;
    private int hospitalization;
    private int surgery;

    private int beforeAdmission;

    private int afterDischarge;

    private int takeCareAtHome;

    private int hospitalizationAllowance;

    private int emergencyTransport;

    private int funeralAllowance;
    private List<InsurancePlanPrice> prices ;
    private  List<InsuranceBenefitDTO> benefits;
}

