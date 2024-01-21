package com.example.insurance.common;

import com.example.insurance.dto.InsurancePlanDTO;
import com.example.insurance.dto.InsurancePlanPriceDTO;
import com.example.insurance.dto.UserAccountDTO;
import com.example.insurance.entity.InsurancePlan;
import com.example.insurance.entity.InsurancePlanPrice;
import com.example.insurance.entity.UserAccount;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MapEntityToDTO {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MapEntityToDTO instance = new MapEntityToDTO();

    private MapEntityToDTO() {
        // private constructor thực thi signleton pattern
    }

    public static MapEntityToDTO getInstance() {
        return instance;
    }

    public UserAccountDTO mapUserAccountToDTO(UserAccount userAccount) {
        return objectMapper.convertValue(userAccount, UserAccountDTO.class);
    }

    public InsurancePlanDTO mapInsurancePlanToDTO(InsurancePlan insurancePlan) {
        return objectMapper.convertValue(insurancePlan, InsurancePlanDTO.class);
    }

    public List<InsurancePlanDTO> mapInsurancePlanListToDTOList(List<InsurancePlan> insurancePlanList) {
        JavaType targetType = objectMapper.getTypeFactory().constructCollectionType(List.class, InsurancePlanDTO.class);
        return objectMapper.convertValue(insurancePlanList, targetType);
    }

    public List<InsurancePlanPriceDTO> mapInsurancePlanPriceListToDTOList(List<InsurancePlanPrice> insurancePlanPriceList) {
        JavaType targetType = objectMapper.getTypeFactory().constructCollectionType(List.class, InsurancePlanPriceDTO.class);
        return objectMapper.convertValue(insurancePlanPriceList, targetType);
    }
}
