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
@Table(name = "insurance_contract")
public class InsuranceContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;

    @OneToOne
    @JoinColumn(name = "registration_form_id", referencedColumnName = "id")
    private RegistrationForm registrationForm;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "status")
    private String status;
}
