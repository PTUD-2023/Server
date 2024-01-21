package com.example.insurance.service;

import com.example.insurance.entity.InsuranceInformation;
import com.example.insurance.repository.InsuranceInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsuranceInformationService {
    private final InsuranceInformationRepository insuranceInformationRepository;

    @Autowired
    public InsuranceInformationService(InsuranceInformationRepository insuranceInformationRepository) {
        this.insuranceInformationRepository = insuranceInformationRepository;
    }

    public InsuranceInformation createInsuranceInformation(InsuranceInformation insuranceInformation) {
        return insuranceInformationRepository.save(insuranceInformation);
    }
}
