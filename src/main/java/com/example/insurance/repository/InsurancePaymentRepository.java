package com.example.insurance.repository;

import com.example.insurance.dto.PaymentReportDTO;
import com.example.insurance.entity.InsurancePayment;
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
public interface InsurancePaymentRepository extends CrudRepository<InsurancePayment,Long> {
    Page<InsurancePayment> findInsurancePaymentByRegistrationFormUserAccountId(Long userAccountId, Pageable pageable);
    Page<InsurancePayment> findAll(Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE InsurancePayment ip SET ip.status = 'dued' " +
            "WHERE ip.deadline < CURRENT_DATE AND ip.status = 'unpaid'")
    void updateOverduePaymentsStatus();

    @Query("SELECT SUM(ip.amount) AS totalRevenue " +
            "FROM InsurancePayment ip " +
            "WHERE FUNCTION('MONTH',ip.paymentDate) = :month AND FUNCTION('YEAR',ip.paymentDate) = :year " +
            "GROUP BY FUNCTION('MONTH',ip.paymentDate), FUNCTION('YEAR',ip.paymentDate)")
    Long getTotalRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT new com.example.insurance.dto.PaymentReportDTO(FUNCTION('MONTH',p.paymentDate), FUNCTION('YEAR',p.paymentDate), SUM(p.amount)) " +
            "FROM InsurancePayment p " +
            "WHERE FUNCTION('YEAR',p.paymentDate) = :year " +
            "GROUP BY FUNCTION('MONTH',p.paymentDate), FUNCTION('YEAR',p.paymentDate)")
    List<PaymentReportDTO> generateMonthlyPaymentReport(@Param("year") int year);
}
