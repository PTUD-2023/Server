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
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;

    @Column(name = "file_type")
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "claim_id", referencedColumnName = "id")
    @JsonBackReference
    private ClaimRequest claimRequest;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;
}
