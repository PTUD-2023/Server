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
@Table(name = "insured_person")
public class InsuredPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birthday")
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "CMND")
    private String CMND;

    @Column(name = "relationship_with_buyers")
    private String relationshipWithBuyers;

    @OneToOne
    @JoinColumn(name = "health_infor_id", referencedColumnName = "id")
    private HealthInformation healthInformation;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeUpdated;

    @Column(name = "status")
    private String status;
}
