package com.example.insurance.repository;

import com.example.insurance.entity.HealthInformation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthInformationRepository extends CrudRepository<HealthInformation, Long> {
}
