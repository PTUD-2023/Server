package com.example.insurance.controller;

import com.example.insurance.dto.InsurancePlanPriceDTO;
import com.example.insurance.service.InsurancePlanPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/insurance-price")
public class InsurancePlanPriceController {
    private final InsurancePlanPriceService insurancePlanPriceService;

    @Autowired
    public InsurancePlanPriceController(InsurancePlanPriceService insurancePlanPriceService) {
        this.insurancePlanPriceService = insurancePlanPriceService;
    }

    @GetMapping("/get/{insurancePlanId}")
    public ResponseEntity<List<InsurancePlanPriceDTO>> getPricesForInsurancePlan(@PathVariable Long insurancePlanId)
    {
        return ResponseEntity.status(HttpStatus.OK).body(insurancePlanPriceService.getPricesForInsurancePlan(insurancePlanId,"activated"));
    }
}
