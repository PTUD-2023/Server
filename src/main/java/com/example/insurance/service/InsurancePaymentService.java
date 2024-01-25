package com.example.insurance.service;

import com.example.insurance.entity.InsurancePayment;
import com.example.insurance.entity.RegistrationForm;
import com.example.insurance.repository.InsurancePaymentRepository;
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
public class InsurancePaymentService {
    private final InsurancePaymentRepository insurancePaymentRepository;
    private static final Logger logger = LoggerFactory.getLogger("Insurance Server");

    @Autowired
    public InsurancePaymentService(InsurancePaymentRepository insurancePaymentRepository) {
        this.insurancePaymentRepository = insurancePaymentRepository;
    }

    public void createInsurancePayment(RegistrationForm registrationForm) {
        // Lấy thời điểm hiện tại
        Date currentDate = new Date();

        // Sử dụng Calendar để thêm 7 ngày
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date deadline = calendar.getTime();
        InsurancePayment insurancePayment = new InsurancePayment();
        insurancePayment.setRegistrationForm(registrationForm);
        insurancePayment.setAmount((registrationForm.getInsuranceInformation().getPlanCost() + registrationForm.getInsuranceInformation().getAdditionalCost()));
        insurancePayment.setDeadline(deadline);
        insurancePaymentRepository.save(insurancePayment);
    }

    public Page<InsurancePayment> getInsurancePaymentByUserAccountId(Long userAccountId, Pageable pageable) {
        return insurancePaymentRepository.findInsurancePaymentByRegistrationFormUserAccountId(userAccountId,pageable);
    }

    public boolean updateInsurancePayment(InsurancePayment insurancePayment) {
        if(insurancePaymentRepository.findById(insurancePayment.getId()).isPresent())
        {
            insurancePaymentRepository.save(insurancePayment);
            return true;
        }
        else{
            return false;
        }
    }

    public InsurancePayment getInsurancePaymentById(Long id) {
        return insurancePaymentRepository.findById(id).orElse(null);
    }

    public Page<InsurancePayment> getAllInsurancePayment(Pageable pageable) {
        return insurancePaymentRepository.findAll(pageable);
    }

    @Scheduled( initialDelay = 1000*60*30,fixedRate = 1000*60*60*2)
    @Transactional
    public void updateOverduePaymentsStatus() {

        insurancePaymentRepository.updateOverduePaymentsStatus();
        logger.info("Cleaned up all unconfirmed accounts in the database");
    }
}
