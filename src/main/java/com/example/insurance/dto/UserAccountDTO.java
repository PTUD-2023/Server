package com.example.insurance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(value = {"id","password","status","timeUpdated"})
public class UserAccountDTO {
    private String email;
    private String lastName;
    private String firstName;
    private String phone;
    private Date birthday;
    private String gender;
    private String avatar;
    private String CMND;
    private String address;
    private Date timeCreated;
    private String role;
}
