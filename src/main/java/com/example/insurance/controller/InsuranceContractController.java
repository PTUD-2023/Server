package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.InsuranceContractService;
import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/v1/insurance-contract")
public class InsuranceContractController {
    private final InsuranceContractService insuranceContractService;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    @Autowired
    public InsuranceContractController(InsuranceContractService insuranceContractService, JwtService jwtService, UserAccountService userAccountService) {
        this.insuranceContractService = insuranceContractService;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllInsuranceContract(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")  int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(insuranceContractService.getAllInsuranceContracts(pageable));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getInsuranceContractByUserAccountId(@RequestHeader(name = "Authorization") String token, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")  int size) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String email = jwtService.extractUsername(jwtToken);
            Optional<UserAccount> userAccount = userAccountService.getUserByEmail(email);
            if(userAccount.isPresent())
            {
                Long userAccountId = userAccount.get().getId();
                Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
                return ResponseEntity.ok(insuranceContractService.getInsuranceContractsByUserAccountId(userAccountId,pageable));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email",new Date()));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
