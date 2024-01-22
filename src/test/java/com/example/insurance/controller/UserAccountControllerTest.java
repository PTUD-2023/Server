package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.dto.UserAccountDTO;
import com.example.insurance.service.JwtService;
import com.example.insurance.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAccountControllerTest {
    @Mock
    private UserAccountService userAccountService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserAccountController userAccountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserInfor_WithValidInput() {
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
    void testGetUserInfor_WithInValidToken() {
        //Arrange
        String invalidToken = "InvalidToken";

        // Act
        ResponseEntity<?> responseEntity = userAccountController.getUserInfor(invalidToken);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void testGetUserInfor_WithNotExistsUser() {
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
    void testGetUserInfor_ShouldInternalError() {
        // Arrange
        String token = "Bearer mockToken";
        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("Simulated internal error"));

        // Act
        ResponseEntity<?> responseEntity = userAccountController.getUserInfor(token);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody()).isNull();
    }
}