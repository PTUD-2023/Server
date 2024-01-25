package com.example.insurance.service;

import com.example.insurance.entity.InsuranceContract;
import com.example.insurance.entity.RegistrationForm;
import com.example.insurance.repository.InsuranceContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class InsuranceContractService {
    private final InsuranceContractRepository insuranceContractRepository;
    private static final Logger logger = LoggerFactory.getLogger("Insurance Server");

    @Autowired
    public InsuranceContractService(InsuranceContractRepository insuranceContractRepository) {
        this.insuranceContractRepository = insuranceContractRepository;
    }

    public InsuranceContract createInsuranceContract(RegistrationForm registrationForm) {
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();
        InsuranceContract insuranceContract = new InsuranceContract();
        insuranceContract.setRegistrationForm(registrationForm);
        insuranceContract.setStartDate(startDate);
        insuranceContract.setEndDate(endDate);
        return insuranceContractRepository.save(insuranceContract);
    }

    public Page<InsuranceContract> getAllInsuranceContracts(Pageable pageable) {
        return insuranceContractRepository.findAll(pageable);
    }

    public Page<InsuranceContract> getInsuranceContractsByUserAccountId(Long userAccountId, Pageable pageable) {
        return insuranceContractRepository.findInsuranceContractByRegistrationFormUserAccountId(userAccountId,pageable);
    }

    public InsuranceContract updateStatusInsuranceContract(Long id, String status) {
        InsuranceContract insuranceContract = insuranceContractRepository.findById(id).orElse(null);
        if(insuranceContract != null && (insuranceContract.getStatus().equals("activated") || insuranceContract.getStatus().equals("pending"))) {
            insuranceContract.setStatus(status);
            insuranceContractRepository.save(insuranceContract);
            return insuranceContract;
        }
        return null;
    }

    public InsuranceContract getInsuranceContractById(Long id) {
        return insuranceContractRepository.findById(id).orElse(null);
    }

    @Scheduled( initialDelay = 1000*60*30,fixedRate = 1000*60*60*2)
    @Transactional
    public void updateOverduePaymentsStatus() {

        insuranceContractRepository.updateExpiredContractsStatus();
        logger.info("Cleaned up all unconfirmed accounts in the database");
    }
}
