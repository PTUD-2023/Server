package com.example.insurance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralReportDTO {
    private Long countInsuranceContract;
    private Long countRegistrationForm;
    private Long countInsuredPerson;
    private Long revenue;
}
