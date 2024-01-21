package com.example.insurance.repository;

import com.example.insurance.entity.InsuranceInformation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceInformationRepository extends CrudRepository<InsuranceInformation, Long> {
}
