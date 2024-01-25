package com.example.insurance.repository;

import com.example.insurance.entity.InsuredPerson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuredPersonRepository extends CrudRepository<InsuredPerson, Long>
{
    @Query("SELECT COUNT( DISTINCT ip.CMND) " +
            "FROM InsuredPerson ip " +
            "WHERE FUNCTION('MONTH',ip.timeCreated) = :month AND FUNCTION('YEAR',ip.timeCreated) = :year")
    Long countUniqueInsuredPersonsByMonth(@Param("month") int month, @Param("year") int year);
}
