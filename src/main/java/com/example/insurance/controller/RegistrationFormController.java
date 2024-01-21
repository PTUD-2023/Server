package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.entity.HealthInformation;
import com.example.insurance.entity.InsuranceInformation;
import com.example.insurance.entity.InsuredPerson;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.*;
import lombok.Getter;
import lombok.Setter;
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
@RequestMapping("/v1/registration-form")
public class RegistrationFormController {
    private final RegistrationFormService registrationFormService;
    private final InsuredPersonService insuredPersonService;
    private final HealthInformationService healthInformationService;
    private final InsuranceInformationService insuranceInformationService;
    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    @Autowired
    public RegistrationFormController(RegistrationFormService registrationFormService, InsuredPersonService insuredPersonService, HealthInformationService healthInformationService, InsuranceInformationService insuranceInformationService, UserAccountService userAccountService, JwtService jwtService) {
        this.registrationFormService = registrationFormService;
        this.insuredPersonService = insuredPersonService;
        this.healthInformationService = healthInformationService;
        this.insuranceInformationService = insuranceInformationService;
        this.userAccountService = userAccountService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRegistrationForm(@RequestHeader(name = "Authorization") String token, @RequestBody NewRegistrationForm newRegistrationForm) {
        InsuredPerson insuredPerson = newRegistrationForm.getInsuredPerson();
        HealthInformation healthInformation = newRegistrationForm.getHealthInformation();
        InsuranceInformation insuranceInformation = newRegistrationForm.getInsuranceInformation();
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String email = jwtService.extractUsername(jwtToken);
            Optional<UserAccount> userAccount = userAccountService.getUserByEmail(email);
            if(userAccount.isPresent())
            {
                Long userAccountId = userAccount.get().getId();
                InsuranceInformation newInsuranceInformation = insuranceInformationService.createInsuranceInformation(insuranceInformation);
                HealthInformation newHealthInformation = healthInformationService.createHealthInformation(healthInformation);
                if(newHealthInformation.getId() > 0) {
                    insuredPerson.setHealthInformation(newHealthInformation);
                    InsuredPerson newInsuredPerson = insuredPersonService.createInsuredPerson(insuredPerson);
                    if(newInsuredPerson.getId() > 0 && newInsuranceInformation.getId() > 0) {
                        registrationFormService.createRegistrationForm(newInsuredPerson,userAccountId,newInsuranceInformation,newRegistrationForm.getNote());
                        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomSuccessResponse("Created registration form successfully","Created"));
                    }
                    else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create registration form");
                    }
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot create health information");
                }
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
    public ResponseEntity<?> getRegistrationFormByUserAccountId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(registrationFormService.getRegistrationFormById(id));
    }

    @GetMapping("/get-by-user")
    public ResponseEntity<?> getRegistrationFormByUser(@RequestHeader(name = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String email = jwtService.extractUsername(jwtToken);
            Optional<UserAccount> userAccount = userAccountService.getUserByEmail(email);
            if(userAccount.isPresent())
            {
                Long userAccountId = userAccount.get().getId();
                return ResponseEntity.status(HttpStatus.OK).body(registrationFormService.getRegistrationFormByUserAccountId(userAccountId));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email",new Date()));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllRegistrationForm(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")  int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(registrationFormService.getAllRegistrationForm(pageable));
    }

    @Getter
    @Setter
    public static class NewRegistrationForm {
        private InsuredPerson insuredPerson;
        private HealthInformation healthInformation;
        private InsuranceInformation insuranceInformation;
        private String note;
    }
}
