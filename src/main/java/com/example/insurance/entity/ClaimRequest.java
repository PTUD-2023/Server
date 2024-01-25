package com.example.insurance.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "claim_requests")
public class ClaimRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "request_date")
    private Date requestDate;

    @Column(name = "accident_date")
    private Date accidentDate;

    @Column(name = "accident_place")
    private String accidentPlace;

    @Column(name = "examination_date")
    private Date examinationDate;

    @Column(name = "hospitalized_date")
    private Date hospitalizedDate;

    @Column(name = "treatment_place")
    private String treatmentPlace;

    @Column(name = "reason")
    private String reason;

    @Column(name = "consequence")
    private String consequence;

    @Column(name = "type_treatment")
    private String typeTreatment;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "death")
    private Boolean death;

    @Column(name = "injured")
    private Boolean injured;

    @Column(name = "transport")
    private Boolean transport;

    @Column(name = "medical_expense")
    private Boolean medicalExpense;

    @Column(name = "benefit")
    private Boolean benefit;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "stk")
    private String stk;

    @Column(name = "bank")
    private String bank;

    @OneToMany(mappedBy = "claimRequest", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
    List<Document> documents;

    @Column(name = "insurance_contract_id")
    private Long insuranceContractId;

    @Column(name = "user_account_id")
    private Long userAccountId;

    @Column(name = "status")
    private String status;


}
