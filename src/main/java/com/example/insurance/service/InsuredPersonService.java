package com.example.insurance.service;

import com.example.insurance.entity.InsuredPerson;
import com.example.insurance.repository.InsuredPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsuredPersonService {
    private final InsuredPersonRepository insuredPersonRepository;

    @Autowired
    public InsuredPersonService(InsuredPersonRepository insuredPersonRepository) {
        this.insuredPersonRepository = insuredPersonRepository;
    }

    public InsuredPerson createInsuredPerson(InsuredPerson insuredPerson) {
        return insuredPersonRepository.save(insuredPerson);
    }

    public InsuredPerson getInsuredPerson(Long id) {
        return insuredPersonRepository.findById(id).orElse(null);
    }

    public void updateInsuredPerson(InsuredPerson insuredPerson) {
        insuredPersonRepository.save(insuredPerson);
    }
}
