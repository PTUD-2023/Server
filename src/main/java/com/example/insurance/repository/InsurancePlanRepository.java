package com.example.insurance.repository;

import com.example.insurance.entity.InsurancePlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePlanRepository extends CrudRepository<InsurancePlan,Long> {
    List<InsurancePlan> findAllByStatus(String status);
    Optional<InsurancePlan> findByIdAndStatus(Long id,String status);
}
