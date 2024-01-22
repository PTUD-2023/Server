package com.example.insurance.dto;

import com.example.insurance.entity.HealthInformation;
import com.example.insurance.entity.InsuredPerson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewInsuredPerson {
    private InsuredPerson insuredPerson;
    private HealthInformation healthInformation;
}