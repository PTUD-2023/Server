package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.dto.UserAccountDTO;
import com.example.insurance.entity.InsurancePayment;
import com.example.insurance.entity.RegistrationForm;
import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAccountControllerTest {
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserAccountController userAccountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserInfor_WithValidInput_ShouldReturnUser() {
        //Arrange
        String token = "Bearer mockToken";
        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");

        UserAccountDTO mockUserAccountDTO = mock(UserAccountDTO.class);
        when(userAccountService.getUserProfileByEmail(anyString())).thenReturn(mockUserAccountDTO);

        // Act
        ResponseEntity<?> responseEntity = userAccountController.getUserInfor(token);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockUserAccountDTO);
    }

    @Test
    void getUserInfor_WithInvalidToken_ShouldReturnUnauthorizedResponse() {
        //Arrange
        String invalidToken = "InvalidToken";

        // Act
        ResponseEntity<?> responseEntity = userAccountController.getUserInfor(invalidToken);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void getUserInfor_WhenUserDoesNotExist_ShouldReturnNotFoundResponse() {
        //Arrange
        String validToken = "Bearer mockToken";

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        when(userAccountService.getUserProfileByEmail(anyString())).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = userAccountController.getUserInfor(validToken);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");
    }

    @Test
    void getUserInfor_ShouldInternalError() {
        // Arrange
        String token = "Bearer mockToken";
        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("Simulated internal error"));

        // Act
        ResponseEntity<?> responseEntity = userAccountController.getUserInfor(token);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void updateUserInfor_ShouldReturnSuccessResponse() {
        // Arrange
        when(userAccountService.updateUserInforByEmail(any())).thenReturn(true);

        // Act
        ResponseEntity<?> responseEntity = userAccountController.updateUserInfor(any());

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Update user information successfully", "UpdateSuccess");
    }

    @Test
    void updateUserInfor_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        when(userAccountService.updateUserInforByEmail(any())).thenReturn(false);

        // Act
        ResponseEntity<?> responseEntity = userAccountController.updateUserInfor(any());

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.INTERNAL_SERVER_ERROR.value(),"UpdateFailed","Update user information failed");
    }

    @Test
    void updateUserPassword_WithValidInput_ShouldReturnSuccessResponse() {
        // Arrange
        String token = "Bearer validToken";
        String newPassword = "newPassword";
        Authentication authentication = mock(Authentication.class);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("newPassword", newPassword);
        String email = "test@example.com";

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userAccountService.updatePasswordByEmail(email,newPassword)).thenReturn(true);
        // Act
        ResponseEntity<?> responseEntity = userAccountController.updateUserPassword(token, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Update user password successfully", "UpdateSuccess");
    }

    @Test
    void updateUserPassword_WithInvalidToken_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String token = "invalidToken";
        Map<String, Object> requestBody = new HashMap<>();

        // Act
        ResponseEntity<?> responseEntity = userAccountController.updateUserPassword(token, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void updateUserPassword_ThrowException_ShouldReturnBadRequestResponse() {
        // Arrange
        String token = "Bearer validToken";
        String newPassword = "newPassword";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("newPassword", newPassword);
        String email = "test@example.com";

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Authentication failed"));

        // Act
        ResponseEntity<?> responseEntity = userAccountController.updateUserPassword(token, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"WrongPassword","Wrong password");
    }

    @Test
    void updateUserPassword_WhenUpdateFail_ShouldInternalErrorResponse() {
        // Arrange
        String token = "Bearer validToken";
        String newPassword = "newPassword";
        InsurancePayment insurancePayment = mock(InsurancePayment.class);
        RegistrationForm registrationForm = mock(RegistrationForm.class);
        Authentication authentication = mock(Authentication.class);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("newPassword", newPassword);
        String email = "test@example.com";

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(insurancePayment.getStatus()).thenReturn("unpaid");
        when(insurancePayment.getRegistrationForm()).thenReturn(registrationForm);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userAccountService.updatePasswordByEmail(email,newPassword)).thenReturn(false);
        // Act
        ResponseEntity<?> responseEntity = userAccountController.updateUserPassword(token, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.INTERNAL_SERVER_ERROR.value(),"UpdateFailed","Update user password failed");
    }
}