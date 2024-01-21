package com.example.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user_accounts")
@Data
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "role")
    private String role;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "birthday")
    @Temporal(TemporalType.DATE)
    private Date birthday;
    @Column(name = "gender")
    private String gender;
    @Column(name = "avatar",columnDefinition = "json")
    private String avatar;
    @Column(name = "CMND")
    private String CMND;
    @Column(name = "address")
    private String address;
    @Column(name = "status")
    private String status;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeUpdated;
}
