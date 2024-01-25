package com.example.insurance.repository;

import com.example.insurance.dto.AgeGroupReportDTO;
import com.example.insurance.dto.PackageReportDTO;
import com.example.insurance.entity.InsuranceContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InsuranceContractRepository extends CrudRepository<InsuranceContract,Long> {
    Page<InsuranceContract> findInsuranceContractByRegistrationFormUserAccountId(Long userAccountId, Pageable pageable);
    Page<InsuranceContract> findAll(Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE InsuranceContract ic SET ic.status = 'expired' " +
            "WHERE CURRENT_DATE NOT BETWEEN ic.startDate AND ic.endDate AND ic.status = 'activated' " )
    void updateExpiredContractsStatus();

    @Query("SELECT COUNT(ic.id) " +
            "FROM InsuranceContract ic " +
            "WHERE FUNCTION('MONTH',ic.createdAt) = :month AND FUNCTION('YEAR',ic.createdAt) = :year " +
            "AND FUNCTION('MONTH',ic.createdAt) = :month AND FUNCTION('YEAR',ic.createdAt) = :year")
    Long countInsuranceContractsByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT NEW com.example.insurance.dto.AgeGroupReportDTO( " +
            "CASE " +
            "WHEN function('FLOOR', function('DATEDIFF', CURRENT_DATE, ip.birthday) / 365) BETWEEN 1 AND 3 THEN '1-3 tuổi' " +
            "WHEN function('FLOOR', function('DATEDIFF', CURRENT_DATE, ip.birthday) / 365) BETWEEN 4 AND 10 THEN '4-10 tuổi' " +
            "WHEN function('FLOOR', function('DATEDIFF', CURRENT_DATE, ip.birthday) / 365) BETWEEN 11 AND 18 THEN '11-18 tuổi' " +
            "WHEN function('FLOOR', function('DATEDIFF', CURRENT_DATE, ip.birthday) / 365) BETWEEN 19 AND 40 THEN '19-40 tuổi' " +
            "WHEN function('FLOOR', function('DATEDIFF', CURRENT_DATE, ip.birthday) / 365) BETWEEN 41 AND 50 THEN '41-50 tuổi' " +
            "WHEN function('FLOOR', function('DATEDIFF', CURRENT_DATE, ip.birthday) / 365) BETWEEN 51 AND 60 THEN '51-60 tuổi' " +
            "ELSE 'Other' " +
            "END AS ageGroup , COUNT(ic.id))  " +
            "FROM InsuranceContract ic " +
            "JOIN ic.registrationForm rf " +
            "JOIN rf.insuredPerson ip " +
            "WHERE function('MONTH',ic.createdAt) = :month AND function('YEAR',ic.createdAt) = :year " +
            "GROUP BY ageGroup")
    List<AgeGroupReportDTO> countContractsByAgeGroup(@Param("month") int month, @Param("year") int year);




    @Query("SELECT new com.example.insurance.dto.PackageReportDTO(r.registrationForm.insuranceInformation.planName, COUNT(r.id)) " +
            "FROM InsuranceContract r " +
            "WHERE function('MONTH',r.createdAt) = :month AND function('YEAR',r.createdAt) = :year " +
            "GROUP BY r.registrationForm.insuranceInformation.planName")
    List<PackageReportDTO> countContractsByPlan(@Param("month") int month, @Param("year") int year);
}
