package com.example.insurance.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "insurance_plan")
public class InsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "duration")
    private String duration;

    @Column(name = "accident_insurance")
    private int accidentInsurance;

    @Column(name = "hospitalization")
    private int hospitalization;

    @Column(name = "surgery")
    private int surgery;

    @Column(name = "before_admission")
    private int beforeAdmission;

    @Column(name = "after_discharge")
    private int afterDischarge;

    @Column(name = "take_care_at_home")
    private int takeCareAtHome;

    @Column(name = "hospitalization_allowance")
    private int hospitalizationAllowance;

    @Column(name = "emergency_transport")
    private int emergencyTransport;

    @Column(name = "funeral_allowance")
    private int funeralAllowance;

    @OneToMany(mappedBy = "insurancePlan", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<InsurancePlanPrice> prices;

    @OneToMany(mappedBy = "insurancePlan", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<InsuranceBenefit> benefits;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "status")
    private String status;
}
