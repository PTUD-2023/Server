package com.example.insurance.dto;

import com.example.insurance.entity.HealthInformation;
import com.example.insurance.entity.InsuranceInformation;
import com.example.insurance.entity.InsuredPerson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewRegistrationForm {
    private InsuredPerson insuredPerson;
    private HealthInformation healthInformation;
    private InsuranceInformation insuranceInformation;
    private String note;
}
