package com.example.insurance.repository;

import com.example.insurance.entity.InsurancePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePaymentRepository extends CrudRepository<InsurancePayment,Long> {
    Page<InsurancePayment> findInsurancePaymentByRegistrationFormUserAccountId(Long userAccountId, Pageable pageable);
    Page<InsurancePayment> findAll(Pageable pageable);
}
