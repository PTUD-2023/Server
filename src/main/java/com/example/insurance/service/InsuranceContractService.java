package com.example.insurance.service;

import com.example.insurance.entity.InsuranceContract;
import com.example.insurance.entity.RegistrationForm;
import com.example.insurance.repository.InsuranceContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class InsuranceContractService {
    private final InsuranceContractRepository insuranceContractRepository;

    @Autowired
    public InsuranceContractService(InsuranceContractRepository insuranceContractRepository) {
        this.insuranceContractRepository = insuranceContractRepository;
    }

    public void createInsuranceContract(RegistrationForm registrationForm) {
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();
        InsuranceContract insuranceContract = new InsuranceContract();
        insuranceContract.setRegistrationForm(registrationForm);
        insuranceContract.setStartDate(startDate);
        insuranceContract.setEndDate(endDate);
        insuranceContractRepository.save(insuranceContract);
    }
}
