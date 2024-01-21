package com.example.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "registration_form")
public class RegistrationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "apply_date")
    private Date applyDate;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "status")
    private String status;

    @Column(name = "user_account_id")
    private Long userAccountId;

    @ManyToOne
    @JoinColumn(name = "insured_person_id", referencedColumnName = "id")
    private InsuredPerson insuredPerson;

    @ManyToOne
    @JoinColumn(name = "insurance_information_id", referencedColumnName = "id")
    private InsuranceInformation insuranceInformation;
}
