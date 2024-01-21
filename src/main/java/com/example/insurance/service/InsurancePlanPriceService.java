package com.example.insurance.service;

import com.example.insurance.common.MapEntityToDTO;
import com.example.insurance.dto.InsurancePlanPriceDTO;
import com.example.insurance.entity.InsurancePlan;
import com.example.insurance.entity.InsurancePlanPrice;
import com.example.insurance.repository.InsurancePlanPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurancePlanPriceService {
    private final InsurancePlanPriceRepository insurancePlanPriceRepository;

    @Autowired
    public InsurancePlanPriceService(InsurancePlanPriceRepository insurancePlanPriceRepository) {
        this.insurancePlanPriceRepository = insurancePlanPriceRepository;
    }

    public List<InsurancePlanPriceDTO> getPricesForInsurancePlan(Long insurancePlanId,String status )
    {
        MapEntityToDTO mapEntityToDTO = MapEntityToDTO.getInstance();
        List<InsurancePlanPrice> insurancePlanPriceList = insurancePlanPriceRepository.findAllByInsurancePlan_IdAndStatus(insurancePlanId,status);
        return mapEntityToDTO.mapInsurancePlanPriceListToDTOList(insurancePlanPriceList);
    }
}
