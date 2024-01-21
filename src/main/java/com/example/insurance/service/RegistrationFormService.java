package com.example.insurance.service;

import com.example.insurance.entity.InsuranceInformation;
import com.example.insurance.entity.InsuredPerson;
import com.example.insurance.entity.RegistrationForm;
import com.example.insurance.repository.RegistrationFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RegistrationFormService {
    private final RegistrationFormRepository registrationFormRepository;

    @Autowired
    public RegistrationFormService(RegistrationFormRepository registrationFormRepository) {
        this.registrationFormRepository = registrationFormRepository;
    }

    public void createRegistrationForm(InsuredPerson insuredPerson, Long userAccountId, InsuranceInformation insuranceInformation,String note) {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setInsuredPerson(insuredPerson);
        registrationForm.setInsuranceInformation(insuranceInformation);
        registrationForm.setUserAccountId(userAccountId);
        Date currentDate = new Date();
        registrationForm.setApplyDate(currentDate);
        registrationForm.setNote(note);
        registrationFormRepository.save(registrationForm);
    }

    public List<RegistrationForm> getRegistrationFormByUserAccountId(Long userAccountId) {
        return registrationFormRepository.findByUserAccountId(userAccountId);
    }


    public RegistrationForm getRegistrationFormById(Long id) {
        return registrationFormRepository.findById(id).orElse(null);
    }

    public Page<RegistrationForm> getAllRegistrationForm(Pageable pageable) {
        return registrationFormRepository.findAll(pageable);
    }

    public RegistrationForm updateStatusRegistrationForm(Long id, String status) {
        RegistrationForm registrationForm = registrationFormRepository.findById(id).orElse(null);
        if(registrationForm != null && registrationForm.getStatus().equals("pending")) {
            registrationForm.setStatus(status);
            registrationFormRepository.save(registrationForm);
            return registrationForm;
        }
        return null;
    }

}
