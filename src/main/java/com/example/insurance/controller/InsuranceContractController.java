package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.component.Utils;
import com.example.insurance.entity.InsuranceContract;
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
    private final Utils utils;

    @Autowired
    public InsuranceContractController(InsuranceContractService insuranceContractService, JwtService jwtService, UserAccountService userAccountService, Utils utils) {
        this.insuranceContractService = insuranceContractService;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
        this.utils = utils;
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

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getInsuranceContractById(@PathVariable Long id) {
        return ResponseEntity.ok(insuranceContractService.getInsuranceContractById(id));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<?> cancelClaimRequest(@PathVariable Long id) {
        InsuranceContract insuranceContract = insuranceContractService.updateStatusInsuranceContract(id,"cancelled");
        if(insuranceContract != null)
        {
            String content = "<div>Gần đây,chúng tôi đã xem xét và đưa ra quyết định như sau:</div> " +
                    "<div>Vì bạn đã vi phạm các điều khoản và quy tắc của chúng tôi nên chúng tôi buộc phải hủy hợp đồng của bạn</div>" +
                    "Nếu có khiếu nại gì vui lòng liên hệ trong 72h để giải quyết. Xin cảm ơn!";
            String subject = "Hợp đồng của bạn đã bị chấm dứt";
            Optional<UserAccount> userAccount = userAccountService.getUserById(insuranceContract.getRegistrationForm().getUserAccountId());
            userAccount.ifPresent(account -> utils.sendResponseEmail(account.getEmail(), subject, content,"static/responseMailTemplate.html",account.getLastName()));
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Cancelled contract successfully","Cancelled"));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"CannotCancelled","Cannot cancel contract",new Date()));
    }

    @PatchMapping("/request-cancel/{id}")
    public ResponseEntity<?> requestCancelClaimRequest(@PathVariable Long id) {
        InsuranceContract insuranceContract = insuranceContractService.updateStatusInsuranceContract(id,"pending");
        if(insuranceContract != null)
        {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Cancelled contract successfully","Cancelled"));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"CannotCancelled","Cannot cancel contract",new Date()));
    }
}
