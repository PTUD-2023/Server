package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.entity.InsurancePayment;
import com.example.insurance.entity.RegistrationForm;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.InsuranceContractService;
import com.example.insurance.service.InsurancePaymentService;
import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class InsurancePaymentControllerTest {
    @Mock
    private InsurancePaymentService insurancePaymentService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private InsuranceContractService insuranceContractService;
    @InjectMocks
    private InsurancePaymentController insurancePaymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInsurancePaymentByUserAccountId_ValidTokenAndUserFound_ShouldReturnInsurancePayments() {
        // Arrange
        String token = "Bearer validToken";
        String email = "test@example.com";
        UserAccount userAccount = mock(UserAccount.class);
        List<InsurancePayment> mockInsurancePayments = Collections.singletonList(new InsurancePayment());
        Page<InsurancePayment> mockPage = new PageImpl<>(mockInsurancePayments);

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(userAccount));
        when(insurancePaymentService.getInsurancePaymentByUserAccountId(anyLong(), any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.getInsurancePaymentByUserAccountId(token, 0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockPage);

        //Verify
        verify(insurancePaymentService, times(1)).getInsurancePaymentByUserAccountId(anyLong(), any(Pageable.class));
    }

    @Test
    void getInsurancePaymentByUserAccountId_WithValidTokenAndUserDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        String token = "Bearer validToken";
        String email = "test@example.com";

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.getInsurancePaymentByUserAccountId(token, 0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");

        //Verify
        verify(insurancePaymentService, times(0)).getInsurancePaymentByUserAccountId(anyLong(), any(Pageable.class));
    }

    @Test
    void getInsurancePaymentByUserAccountId_WithInvalidToken_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String token = "InvalidToken";

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.getInsurancePaymentByUserAccountId(token, 0, 10);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
        //Verify
        verify(insurancePaymentService, times(0)).getInsurancePaymentByUserAccountId(anyLong(), any(Pageable.class));
    }

    @Test
    void payInsurancePayment_WithValidInput_ShouldReturnSuccessResponse() {
        // Arrange
        String token = "Bearer validToken";
        String method = "credit card";
        InsurancePayment insurancePayment = mock(InsurancePayment.class);
        RegistrationForm registrationForm = mock(RegistrationForm.class);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", method);

        String email = "test@example.com";
        UserAccount userAccount = mock(UserAccount.class);
        when(insurancePaymentService.getInsurancePaymentById(anyLong())).thenReturn(insurancePayment);
        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(userAccount));
        Long userAccountId = 1L;
        when(userAccount.getId()).thenReturn(userAccountId);

        when(insurancePayment.getStatus()).thenReturn("unpaid");
        when(insurancePaymentService.updateInsurancePayment(insurancePayment)).thenReturn(true);
        when(insurancePayment.getRegistrationForm()).thenReturn(registrationForm);

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.payInsurancePayment(token, 1L, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Payment successfully", "PaymentSuccess");

        // Verify
        verify(insurancePayment).setPaymentDate(any(Date.class));
        verify(insurancePayment).setPaymentMethod(anyString());
        verify(insurancePayment).setStatus(anyString());
        verify(insuranceContractService).createInsuranceContract(any());
    }

    @Test
    void payInsurancePayment_WithInsurancePaymentDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        String token = "Bearer validToken";
        String method = "credit card";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", method);

        String email = "test@example.com";
        UserAccount userAccount = mock(UserAccount.class);

        when(insurancePaymentService.getInsurancePaymentById(anyLong())).thenReturn(null);
        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(userAccount));
        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.payInsurancePayment(token, 1L, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"InsurancePaymentNotFound","Could not find the insurance payment corresponding to the id");
    }

    @Test
    void payInsurancePayment_WithInsurancePaymentExistsAndStatusNotUnpaid_ShouldReturnBadRequestResponse() {
        // Arrange
        String token = "Bearer validToken";
        String method = "credit card";
        InsurancePayment insurancePayment = mock(InsurancePayment.class);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", method);
        String email = "test@example.com";
        UserAccount userAccount = mock(UserAccount.class);

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(userAccount));
        when(insurancePaymentService.getInsurancePaymentById(anyLong())).thenReturn(insurancePayment);
        when(insurancePayment.getStatus()).thenReturn("paid");

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.payInsurancePayment(token, 1L, requestBody);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"PaymentAlreadyPaid","The insurance payment has already been paid");
    }

    @Test
    void payInsurancePayment_InternalErrorWhenUpdating_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        String token = "Bearer validToken";
        String method = "credit card";
        InsurancePayment insurancePayment = mock(InsurancePayment.class);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", method);

        String email = "test@example.com";
        UserAccount userAccount = mock(UserAccount.class);

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(userAccount));
        when(insurancePaymentService.getInsurancePaymentById(anyLong())).thenReturn(insurancePayment);
        when(insurancePayment.getStatus()).thenReturn("unpaid");
        when(insurancePaymentService.updateInsurancePayment(insurancePayment)).thenReturn(false);

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.payInsurancePayment(token, 1L, requestBody);

        /// Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.INTERNAL_SERVER_ERROR.value(),"InternalServerError","Could not update the insurance payment");

    }

    @Test
    void payInsurancePayment_WhenUserDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        String token = "Bearer validToken";
        String method = "credit card";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", method);

        String email = "test@example.com";

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> responseEntity = insurancePaymentController.payInsurancePayment(token, 1L, requestBody);

        /// Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");

    }
}