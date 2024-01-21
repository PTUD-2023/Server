package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.entity.InsurancePayment;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.InsurancePaymentService;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/insurance-payment")
public class InsurancePaymentController {
    private final InsurancePaymentService insurancePaymentService;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    @Autowired
    public InsurancePaymentController(InsurancePaymentService insurancePaymentService, JwtService jwtService, UserAccountService userAccountService) {
        this.insurancePaymentService = insurancePaymentService;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getInsurancePaymentByUserAccountId(@RequestHeader(name = "Authorization") String token, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")  int size) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String email = jwtService.extractUsername(jwtToken);
            Optional<UserAccount> userAccount = userAccountService.getUserByEmail(email);
            if(userAccount.isPresent())
            {
                Long userAccountId = userAccount.get().getId();
                Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
                return ResponseEntity.ok(insurancePaymentService.getInsurancePaymentByUserAccountId(userAccountId,pageable));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email",new Date()));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> payInsurancePayment(@PathVariable Long id, @RequestBody Map<String, Object> requestBodyMap) {
        String method = (String) requestBodyMap.get("method");
        InsurancePayment insurancePayment = insurancePaymentService.getInsurancePaymentById(id);
        if(insurancePayment != null && insurancePayment.getStatus().equals("unpaid"))
        {
            Date paymentDate = new Date();
            insurancePayment.setPaymentDate(paymentDate);
            insurancePayment.setPaymentMethod(method);
            insurancePayment.setStatus("paid");
            if (insurancePaymentService.updateInsurancePayment(insurancePayment))
            {
                return ResponseEntity.ok(new CustomSuccessResponse("PaymentSuccess","Payment successfully"));
            }
            else
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"InternalServerError","Could not update the insurance payment",new Date()));
            }
        }
        if(insurancePayment != null && !insurancePayment.getStatus().equals("unpaid"))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"PaymentAlreadyPaid","The insurance payment has already been paid",new Date()));
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"InsurancePaymentNotFound","Could not find the insurance payment corresponding to the id",new Date()));
        }
    }
}
