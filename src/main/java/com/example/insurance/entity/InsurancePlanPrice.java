package com.example.insurance.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table(name = "insurance_plan_price")
public class InsurancePlanPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "min_age")
    private int minAge;

    @Column(name = "max_age")
    private int maxAge;

    @Column(name = "price")
    private int price;

    @Column(name = "rate")
    private float rate;

    @ManyToOne
    @JoinColumn(name = "insurance_plan_id", referencedColumnName = "id")
    @JsonBackReference
    private InsurancePlan insurancePlan;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "status")
    private String status;


}
