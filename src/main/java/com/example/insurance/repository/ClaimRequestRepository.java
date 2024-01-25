package com.example.insurance.repository;

import com.example.insurance.dto.CompensationReportDTO;
import com.example.insurance.entity.ClaimRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRequestRepository extends CrudRepository<ClaimRequest,Long> {
    Page<ClaimRequest> findAll(Pageable pageable);
    Page<ClaimRequest> findByUserAccountId(Long userAccountId, Pageable pageable);

    @Query("SELECT new com.example.insurance.dto.CompensationReportDTO(FUNCTION('MONTH',c.requestDate), FUNCTION('YEAR',c.requestDate), SUM(c.amount)) " +
            "FROM ClaimRequest c " +
            "WHERE FUNCTION('YEAR',c.requestDate) = :year AND c.status = 'approved' " +
            "GROUP BY FUNCTION('MONTH',c.requestDate), FUNCTION('YEAR',c.requestDate)")
    List<CompensationReportDTO> generateMonthlyCompensationAmountReport(@Param("year") int year);
}
