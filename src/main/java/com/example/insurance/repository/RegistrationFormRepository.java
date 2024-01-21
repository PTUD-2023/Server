package com.example.insurance.repository;

import com.example.insurance.entity.RegistrationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationFormRepository extends CrudRepository<RegistrationForm, Long> {

    List<RegistrationForm> findByUserAccountId(Long id);
    Page<RegistrationForm> findAll(Pageable pageable);
}
