package com.example.insurance.repository;

import com.example.insurance.entity.InsuredPerson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuredPersonRepository extends CrudRepository<InsuredPerson, Long>
{
}
