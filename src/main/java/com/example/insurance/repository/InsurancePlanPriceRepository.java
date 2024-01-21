package com.example.insurance.repository;

import com.example.insurance.entity.InsurancePlanPrice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsurancePlanPriceRepository extends CrudRepository<InsurancePlanPrice,Long> {
    List<InsurancePlanPrice> findAllByInsurancePlan_IdAndStatus(Long insurancePlanId,String status );
}
