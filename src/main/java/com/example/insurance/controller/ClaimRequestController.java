package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.component.Utils;
import com.example.insurance.dto.NewClaimRequest;
import com.example.insurance.entity.ClaimRequest;
import com.example.insurance.entity.Document;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/claim")
public class ClaimRequestController {
    private final ClaimRequestService claimRequestService;
    private final JwtService jwtService;
    private final UserAccountService userAccountService;
    private final S3Service s3Service;
    private final DocumentService documentService;
    private final Utils utils;

    @Autowired
    public ClaimRequestController(ClaimRequestService claimRequestService, JwtService jwtService, UserAccountService userAccountService, S3Service s3Service, DocumentService documentService, Utils utils) {
        this.claimRequestService = claimRequestService;
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
        this.s3Service = s3Service;
        this.documentService = documentService;
        this.utils = utils;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClaimRequest(@RequestHeader(name = "Authorization") String token, @ModelAttribute NewClaimRequest newClaimRequest, @RequestBody List<MultipartFile> files ) throws IOException {
        String jwtToken = token.substring(7);
        String email = jwtService.extractUsername(jwtToken);
        Optional<UserAccount> userAccount = userAccountService.getUserByEmail(email);
        if(userAccount.isPresent())
        {
            Long userAccountId = userAccount.get().getId();
            newClaimRequest.setUserAccountId(userAccountId);
            ClaimRequest claimRequest = claimRequestService.createClaimRequest(newClaimRequest);
            if(claimRequest.getId() > 0)
            {
                for (MultipartFile file : files) {
                    Document document = new Document();
                    document.setName(file.getOriginalFilename());
                    document.setClaimRequest(claimRequest);
                    document.setFileType(file.getContentType());
                    document.setUrl(s3Service.uploadFileToS3("insurance-doc",file));
                    documentService.createDocument(document);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new CustomSuccessResponse("Create new claim request","Created"));
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"CannotCreate","Could not create new claim request",new Date()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllClaimRequest(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")  int size)
    {
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        return ResponseEntity.ok(claimRequestService.getAllClaimRequest(pageable));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getClaimRequestByUserAccountId(@RequestHeader(name = "Authorization") String token, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")  int size) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String email = jwtService.extractUsername(jwtToken);
            Optional<UserAccount> userAccount = userAccountService.getUserByEmail(email);
            if(userAccount.isPresent())
            {
                Long userAccountId = userAccount.get().getId();
                Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
                return ResponseEntity.ok(claimRequestService.getClaimRequestByUserAccountId(userAccountId,pageable));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email",new Date()));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<?> approveClaimRequest(@PathVariable Long id) {
        ClaimRequest claimRequest = claimRequestService.updateStatusClaimRequest(id,"approved");
        if(claimRequest != null)
        {
            String content = "Gần đây, bạn đã yêu cầu bồi thường bảo hiểm của công ty chúng tôi. Chúng tôi đã xem xét và chấp nhận yêu cầu của bạn. Vui lòng chờ trong 24h để nhận được khoản bồi thường. Xin cảm ơn!";
            String subject = "Yêu cầu bồi thường của bạn đã được duyệt";
            Optional<UserAccount> userAccount = userAccountService.getUserById(claimRequest.getUserAccountId());
            userAccount.ifPresent(account -> utils.sendResponseEmail(account.getEmail(), subject, content,"static/responseMailTemplate.html",account.getLastName()));
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Approved claim request successfully","Approved"));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"CannotApproved","Cannot approve claim request",new Date()));
    }

    @PatchMapping("/refuse/{id}")
    public ResponseEntity<?> refuseClaimRequest(@PathVariable Long id) {
        ClaimRequest claimRequest = claimRequestService.updateStatusClaimRequest(id,"refused");
        if(claimRequest!= null )
        {
            String content = "Gần đây, bạn đã yêu cầu bồi thường bảo hiểm của công ty chúng tôi. Chúng tôi đã xem xét và từ chối yêu cầu của bạn. Lý do bởi vì giấy tờ chứng minh chưa đủ điều kiện. Xin cảm ơn!";
            String subject = "Yêu cầu bồi thường của bạn bị từ chối";
            Optional<UserAccount> userAccount = userAccountService.getUserById(claimRequest.getUserAccountId());
            userAccount.ifPresent(account -> utils.sendResponseEmail(account.getEmail(), subject, content,"static/responseMailTemplate.html",account.getLastName()));
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Refused claim request successfully","Refused"));
        }

        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"CannotRefuse","Cannot refuse claim request",new Date()));
    }
}
