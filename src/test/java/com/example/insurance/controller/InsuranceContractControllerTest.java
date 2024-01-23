package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.entity.InsuranceContract;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.service.InsuranceContractService;
import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class InsuranceContractControllerTest {

    @InjectMocks
    private InsuranceContractController insuranceContractController;

    @Mock
    private InsuranceContractService insuranceContractService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserAccountService userAccountService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInsuranceContractByUserAccountId_WithValidTokenAndUserExisted_ShouldReturnOKResponse() {
        //Arrange
        String token = "Bearer validToken";
        String email = "test@example.com";
        UserAccount mockUserAccount = mock(UserAccount.class);
        List<InsuranceContract> mockInsuranceContract = Collections.singletonList(new InsuranceContract());
        Page<InsuranceContract> mockPage = new PageImpl<>(mockInsuranceContract);

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.of(mockUserAccount));
        when(insuranceContractService.getInsuranceContractsByUserAccountId(anyLong(), any(Pageable.class)))
                .thenReturn(mockPage);

        //Act
        ResponseEntity<?> responseEntity = this.insuranceContractController
                .getInsuranceContractByUserAccountId(token, 0, 10);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockPage);

        //Verify
        verify(insuranceContractService, times(1)).getInsuranceContractsByUserAccountId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetInsuranceContractByUserAccountId_WithValidTokenAndUserDoesNotExist_ShouldReturnNotFoundResponse() {
        //Arrange
        String token = "Bearer validToken";
        String email = "test@example.com";
        UserAccount mockUserAccount = mock(UserAccount.class);

        when(jwtService.extractUsername(token.substring(7))).thenReturn(email);
        when(userAccountService.getUserByEmail(email)).thenReturn(Optional.empty());

        //Act
        ResponseEntity<?> responseEntity = this.insuranceContractController
                .getInsuranceContractByUserAccountId(token, 0, 10);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "ErrorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(), "EmailNotFound", "Could not find the user corresponding to the email");

        //Verify
        verify(insuranceContractService, times(0)).getInsuranceContractsByUserAccountId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetInsuranceContractByUserAccountId_WithInvalidToken_ShouldReturnUnauthorizedResponse() {
        //Arrange
        String token = "InvalidToken";
        UserAccount mockUserAccount = mock(UserAccount.class);


        //Act
        ResponseEntity<?> responseEntity = this.insuranceContractController
                .getInsuranceContractByUserAccountId(token, 0, 10);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();

        //Verify
        verify(insuranceContractService, times(0)).getInsuranceContractsByUserAccountId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllInsuranceContract_WithValidInput_ShouldReturnOkResponse() {
        //Arrange
        List<InsuranceContract> mockInsuranceContract = Collections.singletonList(new InsuranceContract());
        Page<InsuranceContract> mockPage = new PageImpl<>(mockInsuranceContract);

        when(insuranceContractService.getAllInsuranceContracts(any(Pageable.class))).thenReturn(mockPage);

        //Action
        ResponseEntity<?> responseEntity = insuranceContractController.getAllInsuranceContract(0, 10);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockPage);

        //Verify
        verify(insuranceContractService, times(1)).getAllInsuranceContracts(any(Pageable.class));

    }

}
