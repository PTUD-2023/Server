package com.example.insurance.service;

import com.example.insurance.common.MapEntityToDTO;
import com.example.insurance.dto.InsurancePlanDTO;
import com.example.insurance.entity.InsurancePlan;
import com.example.insurance.repository.InsurancePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InsurancePlanService {
    private final InsurancePlanRepository insurancePlanRepository;

    @Autowired
    public InsurancePlanService(InsurancePlanRepository insurancePlanRepository) {
        this.insurancePlanRepository = insurancePlanRepository;
    }

    @Transactional
    public List<InsurancePlanDTO> getAllActivated()
    {
        MapEntityToDTO mapEntityToDTO = MapEntityToDTO.getInstance();
        List<InsurancePlan> insurancePlanList = insurancePlanRepository.findAllByStatus("activated");
        return mapEntityToDTO.mapInsurancePlanListToDTOList(insurancePlanList);
    }

    @Transactional
    public InsurancePlanDTO getInsurancePlanById(Long planId,String status) {
        MapEntityToDTO mapEntityToDTO = MapEntityToDTO.getInstance();
        Optional<InsurancePlan> insurancePlan = insurancePlanRepository.findByIdAndStatus(planId,status);
        return insurancePlan.map(mapEntityToDTO::mapInsurancePlanToDTO).orElse(null);
    }


}
