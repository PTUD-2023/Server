package com.example.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "confirm_code")
@Data
public class ConfirmCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "user_account_id",referencedColumnName = "id")
    private UserAccount userAccount;
    @Column(nullable = false,unique = true)
    private String code;
    @Column(nullable = false)
    private Instant expiryDate;
}
