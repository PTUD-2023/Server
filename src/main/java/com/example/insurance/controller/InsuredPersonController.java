package com.example.insurance.controller;

import com.example.insurance.dto.NewInsuredPerson;
import com.example.insurance.entity.HealthInformation;
import com.example.insurance.entity.InsuredPerson;
import com.example.insurance.service.HealthInformationService;
import com.example.insurance.service.InsuredPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/insured-person")
public class InsuredPersonController {
    private final InsuredPersonService insuredPersonService;
    private final HealthInformationService healthInformationService;

    @Autowired
    public InsuredPersonController(InsuredPersonService insuredPersonService, HealthInformationService healthInformationService) {
        this.insuredPersonService = insuredPersonService;
        this.healthInformationService = healthInformationService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createInsuredPerson(@RequestBody NewInsuredPerson newInsuredPerson) {
        InsuredPerson insuredPerson = newInsuredPerson.getInsuredPerson();
        HealthInformation healthInformation = newInsuredPerson.getHealthInformation();

        HealthInformation newHealthInformation = healthInformationService.createHealthInformation(healthInformation);
        if(newHealthInformation.getId() > 0) {
            insuredPerson.setHealthInformation(newHealthInformation);
            if(insuredPersonService.createInsuredPerson(insuredPerson).getId() > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Create insured person successfully");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create insured person");
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create health information");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getInsuredPerson(@PathVariable Long id) {
        InsuredPerson insuredPerson = insuredPersonService.getInsuredPerson(id);
        if(insuredPerson != null) {
            return ResponseEntity.status(HttpStatus.OK).body(insuredPerson);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot find insured person");
        }
    }


}
