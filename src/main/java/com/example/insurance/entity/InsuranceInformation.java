package com.example.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "insurance_information")
public class InsuranceInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planName;

    private int planCost;

    private int additionalCost;

    private int accidentInsurance;

    private int hospitalization;

    private int surgery;

    private int beforeAdmission;

    private int afterDischarge;

    private int takeCareAtHome;

    private int hospitalizationAllowance;

    private int emergencyTransport;

    private int funeralAllowance;

    private int outpatientTreatmentMax;

    private int outpatientTreatment;

    private int dentalCare;

    private int maternity;

    private int death;

    private String status;
}
