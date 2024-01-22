package com.example.insurance.repository;

import com.example.insurance.entity.InsuranceContract;
import com.example.insurance.entity.InsurancePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceContractRepository extends CrudRepository<InsuranceContract,Long> {
    Page<InsuranceContract> findInsuranceContractByRegistrationFormUserAccountId(Long userAccountId, Pageable pageable);
    Page<InsuranceContract> findAll(Pageable pageable);
}
