package com.example.insurance.repository;

import com.example.insurance.entity.InsuranceContract;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceContractRepository extends CrudRepository<InsuranceContract,Long> {

}
