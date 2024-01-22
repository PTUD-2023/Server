package com.example.insurance.repository;

import com.example.insurance.entity.RegistrationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegistrationFormRepository extends CrudRepository<RegistrationForm, Long> {

    Page<RegistrationForm> findByUserAccountId(Long id,Pageable pageable);
    Page<RegistrationForm> findAll(Pageable pageable);

}
