package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.dto.UserAccountDTO;
import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserAccountController {
    private final UserAccountService userAccountService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserAccountController(UserAccountService userAccountService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userAccountService = userAccountService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/infor")
    public ResponseEntity<?> getUserInfor(@RequestHeader(name = "Authorization") String token)
    {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String email = jwtService.extractUsername(jwtToken);
                UserAccountDTO userAccountDTO = userAccountService.getUserProfileByEmail(email);
                if(userAccountDTO != null)
                {
                    return ResponseEntity.status(HttpStatus.OK).body(userAccountDTO);
                }
                else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email",new Date()));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserInfor( @RequestBody UserAccountDTO userAccountDTO)
    {
        if(userAccountService.updateUserInforByEmail(userAccountDTO))
        {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Update user information successfully","UpdateSuccess"));
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"UpdateFailed","Update user information failed",new Date()));
        }
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updateUserPassword(@RequestHeader(name = "Authorization") String token, @RequestBody Map<String, Object> requestBodyMap)
    {
        String oldPassword = (String) requestBodyMap.get("oldPassword");
        String newPassword = (String) requestBodyMap.get("newPassword");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String email = jwtService.extractUsername(jwtToken);
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, oldPassword));
                if(authentication.isAuthenticated())
                {
                    if(userAccountService.updatePasswordByEmail(email,newPassword))
                    {
                        return ResponseEntity.status(HttpStatus.OK).body(new CustomSuccessResponse("Update user password successfully","UpdateSuccess"));
                    }
                    else{
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"UpdateFailed","Update user password failed",new Date()));
                    }
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(),"WrongPassword","Wrong password",new Date()));
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
