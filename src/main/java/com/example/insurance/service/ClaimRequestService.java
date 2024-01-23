package com.example.insurance.service;

import com.example.insurance.common.MapDTO2Entity;
import com.example.insurance.dto.NewClaimRequest;
import com.example.insurance.entity.ClaimRequest;
import com.example.insurance.repository.ClaimRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ClaimRequestService {
    private final ClaimRequestRepository claimRequestRepository;

    @Autowired
    public ClaimRequestService(ClaimRequestRepository claimRequestRepository) {
        this.claimRequestRepository = claimRequestRepository;
    }

    public ClaimRequest createClaimRequest(NewClaimRequest newClaimRequest) {
        Date date = new Date();
        ClaimRequest claimRequest = MapDTO2Entity.getInstance().mapDTO2ClaimRequest(newClaimRequest);
        claimRequest.setRequestDate(date);
        return claimRequestRepository.save(claimRequest);
    }

    public Page<ClaimRequest> getClaimRequestByUserAccountId(Long userAccountId, Pageable pageable) {
        return claimRequestRepository.findByUserAccountId(userAccountId, pageable);
    }

    @Transactional
    public Page<ClaimRequest> getAllClaimRequest(Pageable pageable) {
        return claimRequestRepository.findAll(pageable);
    }

    public ClaimRequest updateStatusClaimRequest(Long id, String status) {
        ClaimRequest claimRequest = claimRequestRepository.findById(id).orElse(null);
        if(claimRequest != null && claimRequest.getStatus().equals("pending")) {
            claimRequest.setStatus(status);
            claimRequestRepository.save(claimRequest);
            return claimRequest;
        }
        return null;
    }


}
