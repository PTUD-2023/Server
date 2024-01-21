package com.example.insurance.service;

import com.example.insurance.entity.HealthInformation;
import com.example.insurance.repository.HealthInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthInformationService {
    private final HealthInformationRepository healthInformationRepository;

    @Autowired
    public HealthInformationService(HealthInformationRepository healthInformationRepository) {
        this.healthInformationRepository = healthInformationRepository;
    }

    public HealthInformation getHealthInformationById(Long id) {
        return healthInformationRepository.findById(id).orElse(null);
    }

    public HealthInformation createHealthInformation(HealthInformation healthInformation) {
        return healthInformationRepository.save(healthInformation);
    }
}
