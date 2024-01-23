package com.example.insurance.repository;

import com.example.insurance.entity.ClaimRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRequestRepository extends CrudRepository<ClaimRequest,Long> {
    Page<ClaimRequest> findAll(Pageable pageable);
    Page<ClaimRequest> findByUserAccountId(Long userAccountId, Pageable pageable);
}
