package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.common.ReadEmailTemplate;
import com.example.insurance.dto.NewRegistrationForm;
import com.example.insurance.entity.*;
import com.example.insurance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
    private final InsurancePaymentService insurancePaymentService;
    private final JwtService jwtService;
    private final MyEmailService myEmailService;

    @Autowired
    public RegistrationFormController(RegistrationFormService registrationFormService, InsuredPersonService insuredPersonService, HealthInformationService healthInformationService, InsuranceInformationService insuranceInformationService, UserAccountService userAccountService, InsurancePaymentService insurancePaymentService, JwtService jwtService, MyEmailService myEmailService) {
        this.registrationFormService = registrationFormService;
        this.insuredPersonService = insuredPersonService;
        this.healthInformationService = healthInformationService;
        this.insuranceInformationService = insuranceInformationService;
        this.userAccountService = userAccountService;
        this.insurancePaymentService = insurancePaymentService;
        this.jwtService = jwtService;
        this.myEmailService = myEmailService;
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

<<<<<<< HEAD
    @PatchMapping("/approve/{id}")
    public ResponseEntity<?> approveRegistrationForm(@PathVariable Long id) {
        RegistrationForm registrationForm = registrationFormService.updateStatusRegistrationForm(id,"approved");
        if(registrationForm != null)
        {
            sendResponseEmail(registrationForm,"Đơn đăng ký bảo hiểm của bạn đã được duyệt","Gần đây, bạn đã đăng ký bảo hiểm của công ty chúng tôi. Để hoàn thành quy trình đăng ký, vui lòng thanh toán hóa đơn để chúng tôi cấp hợp đồng bảo hiểm cho bạn.");
            insurancePaymentService.createInsurancePayment(registrationForm);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Approved registration form successfully","Approved"));
        }

        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"CannotApproved","Cannot approve registration form",new Date()));
=======
    @Getter
    @Setter
    public static class NewRegistrationForm {
        private InsuredPerson insuredPerson;
        private HealthInformation healthInformation;
        private InsuranceInformation insuranceInformation;
        private String note;
>>>>>>> ef7c9815591804a1455746f7b759cce2349f06ea
    }

    @PatchMapping("/refuse/{id}")
    public ResponseEntity<?> refuseRegistrationForm(@PathVariable Long id) {
        RegistrationForm registrationForm = registrationFormService.updateStatusRegistrationForm(id,"refused");
        if(registrationForm != null)
        {
            sendResponseEmail(registrationForm,"Đơn đăng ký bảo hiểm của bạn đã bị từ chối","Gần đây, bạn đã đăng ký bảo hiểm của công ty chúng tôi. Tuy nhiên, khi xem xét thì chúng tôi nhận thấy bạn không đủ điều kiện để tham gia chương trình bảo hiểm của chúng tôi.");
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Refuse registration form successfully","Refused"));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"CannotRefuse","Cannot refuse registration form",new Date()));
    }

    private void sendResponseEmail(RegistrationForm registrationForm,String subject,String content)
    {
        String to = registrationForm.getInsuredPerson().getEmail();
        StringBuilder body = new StringBuilder();
        Resource resource = new ClassPathResource("static/responseMailTemplate.html");
        String htmlContent = ReadEmailTemplate.read(resource);
        htmlContent = htmlContent.replace("${lastName}",registrationForm.getInsuredPerson().getName());
        htmlContent = htmlContent.replace("${content}",content);
        body.append(htmlContent);
        new Thread(()->{
            try
            {
                myEmailService.sendSimpleMessage(to,subject,body.toString());
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }).start();
    }

}
