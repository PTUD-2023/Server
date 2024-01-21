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
@Table(name = "health_information")
public class HealthInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String injured;

    private String medicalTreatment;

    private String radiationTreatment;

    private String neurologicalTreatment;

    private String cardiovascularTreatment;

    private String metabolicTreatment;

    private String infectiousDiseaseTreatment;

    private String disability;

    private String strokeOrAsthma;

    private String pregnant;

    private String complicationHistory;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeUpdated;
}
