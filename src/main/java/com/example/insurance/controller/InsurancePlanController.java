package com.example.insurance.controller;


import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.dto.InsurancePlanDTO;
import com.example.insurance.service.InsurancePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/insurance-plan")
public class InsurancePlanController {
    private final InsurancePlanService insurancePlanService;

    @Autowired
    public InsurancePlanController(InsurancePlanService insurancePlanService) {
        this.insurancePlanService = insurancePlanService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<InsurancePlanDTO>> getAllInsurancePlan()
    {
        return ResponseEntity.status(HttpStatus.OK).body(insurancePlanService.getAllActivated());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPlanWithPrices(@PathVariable Long id) {
        InsurancePlanDTO insurancePlanDTO = insurancePlanService.getInsurancePlanById(id,"activated");
        if(insurancePlanDTO != null)
        {
            return ResponseEntity.status(HttpStatus.OK).body(insurancePlanDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), "PlanNotFound", "Cannot find insurance plan with id: " + id,new Date()));
        }
    }
}
