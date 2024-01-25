package com.example.insurance.repository;

import com.example.insurance.dto.SaleReportDTO;
import com.example.insurance.entity.RegistrationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RegistrationFormRepository extends CrudRepository<RegistrationForm, Long> {

    Page<RegistrationForm> findByUserAccountId(Long id,Pageable pageable);
    Page<RegistrationForm> findAll(Pageable pageable);

    @Query("SELECT NEW com.example.insurance.dto.SaleReportDTO(r.insuranceInformation.planName, COUNT(r.id)) " +
            "FROM RegistrationForm r " +
            "WHERE FUNCTION('MONTH', r.applyDate) = :month AND FUNCTION('YEAR', r.applyDate) = :year " +
            "GROUP BY r.insuranceInformation.planName " +
            "ORDER BY COUNT(r.id) DESC")
    List<SaleReportDTO> findMostRegisteredPackageByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(r.id) " +
            "FROM RegistrationForm r " +
            "WHERE FUNCTION('MONTH',r.createdAt) = :month AND FUNCTION('YEAR',r.createdAt) = :year " +
            "AND FUNCTION('MONTH',r.createdAt) = :month AND FUNCTION('YEAR',r.createdAt) = :year")
    Long countRegistrationFormsByMonth(@Param("month") int month, @Param("year") int year);
}
