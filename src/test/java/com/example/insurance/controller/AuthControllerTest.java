package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.component.Base64Encoding;
import com.example.insurance.dto.ConfirmCodeRequest;
import com.example.insurance.dto.SignUpRequest;
import com.example.insurance.dto.SignUpResponse;
import com.example.insurance.dto.TokenRefreshRequest;
import com.example.insurance.entity.*;
import com.example.insurance.exception.CustomException;
import com.example.insurance.service.*;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private ConfirmCodeService confirmCodeService;
    @Mock
    private MyEmailService myEmailService;
    @Mock
    private Environment env;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUpNewAccount_WithValidInput_ShouldReturnCreatedResponse() {
        // Arrange
        UserAccount userAccount = mock(UserAccount.class);
        AuthController mockAuthController = mock(AuthController.class);
        ConfirmCode mockConfirmCode = mock(ConfirmCode.class);

        when(userAccountService.signUpNewAccount(any(UserAccount.class))).thenReturn(userAccount);

        when(userAccount.getId()).thenReturn(1L);
        when(userAccount.getEmail()).thenReturn("mockEmail");
        when(userAccount.getLastName()).thenReturn("lastName");

        when(confirmCodeService.createConfirmCode(anyString())).thenReturn(mockConfirmCode);
        when(mockConfirmCode.getCode()).thenReturn("123456");

        mockAuthController.sendConfirmCodeEmail(any());

        // Act
        ResponseEntity<?> responseEntity = authController.signUpNewAccount(userAccount);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Account registration has email " + userAccount.getEmail() + " successful", "AccountIsRegistered");

        // Verify
        verify(mockAuthController).sendConfirmCodeEmail(any());
    }

    @Test
    void signUpNewAccount_WithInvalidUser_ShouldReturnBadRequestResponse() {
        // Arrange
        UserAccount userAccount = mock(UserAccount.class);

        when(userAccountService.signUpNewAccount(any(UserAccount.class))).thenReturn(userAccount);
        when(userAccount.getId()).thenReturn(-1L);

        // Act
        ResponseEntity<?> responseEntity = authController.signUpNewAccount(userAccount);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"RegistrationFailed","Account registration failed");
    }

    @Test
    void signIn_WithValidInput_ShouldReturnSuccessResponse() {
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        UserAccount userAccount = mock(UserAccount.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        when(signUpRequest.getEmail()).thenReturn("mockEmail");
        when(signUpRequest.getPassword()).thenReturn("mockPassword");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userAccountService.getUserByEmail(any())).thenReturn(Optional.of(userAccount));
        when(userAccount.getStatus()).thenReturn("activated");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("test_jwt");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);
        when(refreshToken.getToken()).thenReturn("test_token");

        ResponseEntity<?> responseEntity = authController.signIn(signUpRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(SignUpResponse.class)
                .extracting("accessToken", "refreshToken", "message", "key")
                .containsExactly(jwtService.generateToken(anyString()), refreshToken.getToken(),"Login is successfully", "Success");
    }

    @Test
    void signIn_WithEmptyEmail_ShouldReturnBadRequestResponse() {
        // Arrange
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        when(signUpRequest.getEmail()).thenReturn("");
        when(signUpRequest.getPassword()).thenReturn("mockPassword");

        // Act
        ResponseEntity<?> responseEntity = authController.signIn(signUpRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"LackOfEmailOrPassword","Missing Email or password");

    }

    @Test
    void signIn_WithEmptyPassword_ShouldReturnBadRequestResponse() {
        // Arrange
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        when(signUpRequest.getEmail()).thenReturn("mockEmail");
        when(signUpRequest.getPassword()).thenReturn("");

        // Act
        ResponseEntity<?> responseEntity = authController.signIn(signUpRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"LackOfEmailOrPassword","Missing Email or password");

    }

    @Test
    void signIn_WithUnverifiedAccount_ShouldReturnBadRequestResponse() {
        // Arrange
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        UserAccount userAccount = mock(UserAccount.class);

        when(signUpRequest.getEmail()).thenReturn("mockEmail");
        when(signUpRequest.getPassword()).thenReturn("mockPassword");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userAccountService.getUserByEmail(any())).thenReturn(Optional.of(userAccount));
        when(userAccount.getStatus()).thenReturn("not_activated");

        // Act
        ResponseEntity<?> responseEntity = authController.signIn(signUpRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"UnverifiedAccount","Account has not verified email");
    }

    @Test
    void signIn_WithInvalidCredentials_ShouldReturnNotFoundResponse() {
        // Arrange
        SignUpRequest signUpRequest = mock(SignUpRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        UserAccount userAccount = mock(UserAccount.class);

        when(signUpRequest.getEmail()).thenReturn("mockEmail");
        when(signUpRequest.getPassword()).thenReturn("mockPassword");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userAccountService.getUserByEmail(any())).thenReturn(Optional.of(userAccount));
        when(userAccount.getStatus()).thenReturn("activated");
        when(authentication.isAuthenticated()).thenReturn(false);
        // Act
        ResponseEntity<?> responseEntity = authController.signIn(signUpRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");
    }

    @Test
    void refreshToken_WithValidRefreshToken_ShouldReturnSuccessResponse() {
        // Arrange
        TokenRefreshRequest tokenRefreshRequest = mock(TokenRefreshRequest.class);
        RefreshToken refreshToken = mock(RefreshToken.class);
        UserAccount userAccount = mock(UserAccount.class);

        when(tokenRefreshRequest.getRefreshToken()).thenReturn("token");

        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any())).thenReturn(refreshToken);
        when(refreshToken.getUserAccount()).thenReturn(userAccount);
        when(userAccount.getEmail()).thenReturn("mockEmail");
        when(jwtService.generateToken(anyString())).thenReturn("new-token");

        // Act
        ResponseEntity<?> responseEntity = authController.refreshToken(tokenRefreshRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(SignUpResponse.class)
                .extracting("accessToken", "refreshToken")
                .containsExactly("new-token", "token");
    }

    @Test
    void refreshToken_WithValidRefreshToken_ShouldThrowException() {
        // Arrange
        TokenRefreshRequest tokenRefreshRequest = mock(TokenRefreshRequest.class);
        when(tokenRefreshRequest.getRefreshToken()).thenReturn("token");
        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.empty());

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> authController.refreshToken(tokenRefreshRequest));

        // Assert exception message
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exception.getErrorKey()).isEqualTo("RefreshTokenIsInexist");
        assertThat(exception.getMessage()).isEqualTo("Refresh token does not exist");
    }

    @Test
    void sendConfirmCodeEmail_ShouldHandleException() throws MessagingException {
        // Arrange
        UserAccount mockUserAccount = mock(UserAccount.class);
        ConfirmCode mockConfirmCode = mock(ConfirmCode.class);

        when(confirmCodeService.createConfirmCode(anyString())).thenReturn(mockConfirmCode);
        when(mockUserAccount.getEmail()).thenReturn("mockEmail");
        when(mockUserAccount.getLastName()).thenReturn("lastName");
        when(mockConfirmCode.getCode()).thenReturn("123456");
        doThrow(new RuntimeException("Error sending email")).when(myEmailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // Act
        authController.sendConfirmCodeEmail(mockUserAccount);

        // Assert
        // Verify that the exception is printed to the console
    }

    @Test
    void confirmSignUpAccount_WithValidInput_ShouldReturnSuccessResponse() {
        ConfirmCodeRequest confirmCodeRequest = mock(ConfirmCodeRequest.class);
        ConfirmCode confirmCode = mock(ConfirmCode.class);
        UserAccount userAccount = mock(UserAccount.class);
        String email = "testEmail";
        String emailRequest = "testEmail";
        String encodedEmail = Base64Encoding.encodeStringToBase64(emailRequest);

        when(confirmCodeRequest.getCode()).thenReturn("123456");
        when(confirmCodeService.findByCode(anyString())).thenReturn(Optional.of(confirmCode));
        when(confirmCodeService.isExpiredCode(any())).thenReturn(false);
        when(confirmCode.getUserAccount()).thenReturn(userAccount);

        when(userAccount.getEmail()).thenReturn(email);
        when(confirmCodeRequest.getEmail()).thenReturn(encodedEmail);
        doNothing().when(userAccountService).updateStatusByEmail(anyString(),anyString());
        doNothing().when(confirmCodeService).deleteByCode(anyString());

        // Act
        ResponseEntity<?> responseEntity = authController.confirmSignUpAccount(confirmCodeRequest);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message")
                .isEqualTo("Account confirmed successfully");
    }

    @Test
    void confirmSignUpAccount_WithConfirmCodeDoesNotExist_ShouldThrowException() {
        // Arrange
        ConfirmCodeRequest confirmCodeRequest = mock(ConfirmCodeRequest.class);
        when(confirmCodeService.findByCode(anyString())).thenReturn(Optional.empty());

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> authController.confirmSignUpAccount(confirmCodeRequest));

        // Assert exception message
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exception.getErrorKey()).isEqualTo("ConfirmCodeIsIncorrect");
        assertThat(exception.getMessage()).isEqualTo("Verification code is wrong");
    }

    @Test
    void confirmSignUpAccount_WithExpiredConfirmCode_ShouldThrowException() {
        // Arrange
        ConfirmCodeRequest confirmCodeRequest = mock(ConfirmCodeRequest.class);
        ConfirmCode confirmCode = mock(ConfirmCode.class);
        when(confirmCodeRequest.getCode()).thenReturn("123456");
        when(confirmCodeService.findByCode(anyString())).thenReturn(Optional.of(confirmCode));
        when(confirmCodeService.isExpiredCode(any())).thenReturn(true);

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> authController.confirmSignUpAccount(confirmCodeRequest));

        // Assert exception message
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exception.getErrorKey()).isEqualTo("ConfirmCodeIsExpired");
        assertThat(exception.getMessage()).isEqualTo("Verification code expired");
    }

    @Test
    void confirmSignUpAccount_WithInvalidEmailRequest_ShouldThrowException() {
        ConfirmCodeRequest confirmCodeRequest = mock(ConfirmCodeRequest.class);
        ConfirmCode confirmCode = mock(ConfirmCode.class);
        UserAccount userAccount = mock(UserAccount.class);
        String email = "testEmail";
        String emailRequest = "testEmailRequest";
        String encodedEmail = Base64Encoding.encodeStringToBase64(emailRequest);

        when(confirmCodeRequest.getCode()).thenReturn("123456");
        when(confirmCodeService.findByCode(anyString())).thenReturn(Optional.of(confirmCode));
        when(confirmCodeService.isExpiredCode(any())).thenReturn(false);
        when(confirmCode.getUserAccount()).thenReturn(userAccount);

        when(userAccount.getEmail()).thenReturn(email);
        when(confirmCodeRequest.getEmail()).thenReturn(encodedEmail);

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> authController.confirmSignUpAccount(confirmCodeRequest));

        // Assert exception message
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exception.getErrorKey()).isEqualTo("ConfirmCodeIsIncorrect");
        assertThat(exception.getMessage()).isEqualTo("Verification code is wrong");
    }

    @Test
    void resendConfirmCode_WithValidInput_ShouldReturnSuccessResponse() {
        ConfirmCode confirmCode = mock(ConfirmCode.class);
        UserAccount userAccount = mock(UserAccount.class);
        AuthController mockAuthController = mock(AuthController.class);
        String emailRequest = "testEmail";
        String encodedEmail = Base64Encoding.encodeStringToBase64(emailRequest);

        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(userAccount));
        when(userAccount.getStatus()).thenReturn("not_activated");
        when(userAccount.getEmail()).thenReturn(emailRequest);
        when(userAccount.getLastName()).thenReturn("lastName");
        confirmCodeService.deleteAllByUserAccount(userAccount);
        mockAuthController.sendConfirmCodeEmail(userAccount);

        when(confirmCodeService.createConfirmCode(anyString())).thenReturn(confirmCode);
        when(confirmCode.getCode()).thenReturn("123456");

        // Act
        ResponseEntity<?> responseEntity = authController.resendConfirmCode(encodedEmail);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message")
                .isEqualTo("Resend confirmation code successfully");
    }

    @Test
    void resendConfirmCode_WithUserDoesNotExist_ShouldThrowException() {
        String emailRequest = "testEmail";
        String encodedEmail = Base64Encoding.encodeStringToBase64(emailRequest);

        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> authController.resendConfirmCode(encodedEmail));

        // Assert exception message
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exception.getErrorKey()).isEqualTo("InexistAccount");
        assertThat(exception.getMessage()).isEqualTo("Account does not exist");
    }

    @Test
    void resendConfirmCode_WithActivatedUserAccount_ShouldThrowException() {
        String emailRequest = "testEmail";
        String encodedEmail = Base64Encoding.encodeStringToBase64(emailRequest);
        UserAccount userAccount = mock(UserAccount.class);

        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(userAccount));
        when(userAccount.getStatus()).thenReturn("activated");

        // Act
        CustomException exception = assertThrows(CustomException.class, () -> authController.resendConfirmCode(encodedEmail));

        // Assert exception message
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exception.getErrorKey()).isEqualTo("VerifiedAccount");
        assertThat(exception.getMessage()).isEqualTo("Account has email authentication");
    }

}