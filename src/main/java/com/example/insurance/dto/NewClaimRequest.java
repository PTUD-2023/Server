package com.example.insurance.dto;

import lombok.Data;


@Data
public class NewClaimRequest {
    private Integer amount;
    private String accidentDate;

    private String accidentPlace;

    private String examinationDate;

    private String hospitalizedDate;

    private String treatmentPlace;

    private String reason;

    private String consequence;

    private String typeTreatment;

    private String endDate;

    private String startDate;

    private Boolean death;

    private Boolean injured;

    private Boolean transport;

    private Boolean medicalExpense;

    private Boolean benefit;

    private Long insuranceContractId;

    private Long userAccountId;
}
