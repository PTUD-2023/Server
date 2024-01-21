package com.example.insurance.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "insurance_benefit", schema = "health_insurance")
public class InsuranceBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "benefit_name")
    private String benefitName;
    @Column(name = "min_age")
    private int minAge;
    @Column(name = "max_age")
    private int maxAge;
    @Column(name = "price")
    private int price;
    @Column(name = "max_payout")
    private int maxPayout;
    @Column(name = "per_times_payout")
    private int perTimesPayout;
    @ManyToOne
    @JoinColumn(name = "insurance_plan_id", referencedColumnName = "id")
    @JsonBackReference
    private InsurancePlan insurancePlan;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "status")
    private String status;
}
