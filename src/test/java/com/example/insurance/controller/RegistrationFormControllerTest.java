package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.dto.NewRegistrationForm;
import com.example.insurance.entity.*;
import com.example.insurance.service.*;
import jakarta.mail.MessagingException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegistrationFormControllerTest {
    @Mock
    private RegistrationFormService registrationFormService;
    @Mock
    private InsuredPersonService insuredPersonService;
    @Mock
    private HealthInformationService healthInformationService;
    @Mock
    private InsuranceInformationService insuranceInformationService;
    @Mock
    private UserAccountService userAccountService;
    @Mock
    private InsurancePaymentService insurancePaymentService;
    @Mock
    private JwtService jwtService;
    @Mock
    private MyEmailService myEmailService;

    @InjectMocks
    private RegistrationFormController registrationFormController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createRegistrationForm_WithValidInput_ShouldReturnCreatedResponse() {
        // Arrange
        String token = "Bearer mockToken"; //Token == null and token start with Bearer
        NewRegistrationForm newRegistrationForm = mock(NewRegistrationForm.class);
        InsuredPerson insuredPerson = mock(InsuredPerson.class);
        HealthInformation healthInformation = mock(HealthInformation.class);
        InsuranceInformation insuranceInformation = mock(InsuranceInformation.class);

        when(newRegistrationForm.getInsuredPerson()).thenReturn(insuredPerson);
        when(newRegistrationForm.getHealthInformation()).thenReturn(healthInformation);
        when(newRegistrationForm.getInsuranceInformation()).thenReturn(insuranceInformation);
        when(newRegistrationForm.getNote()).thenReturn("note");

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");

        UserAccount mockUserAccount = mock(UserAccount.class);
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUserAccount));

        Long mockUserAccountId = 1L;
        when(mockUserAccount.getId()).thenReturn(mockUserAccountId);

        InsuranceInformation mockNewInsuranceInformation = mock(InsuranceInformation.class);
        when(insuranceInformationService.createInsuranceInformation(any())).thenReturn(mockNewInsuranceInformation);
        when(mockNewInsuranceInformation.getId()).thenReturn(1L);

        HealthInformation mockNewHealthInformation = mock(HealthInformation.class);
        when(healthInformationService.createHealthInformation(any())).thenReturn(mockNewHealthInformation);
        when(mockNewHealthInformation.getId()).thenReturn(1L);

        InsuredPerson mockNewInsuredPerson = mock(InsuredPerson.class);
        when(insuredPersonService.createInsuredPerson(any())).thenReturn(mockNewInsuredPerson);
        when(mockNewInsuredPerson.getId()).thenReturn(1L);

        registrationFormService.createRegistrationForm(mockNewInsuredPerson, mockUserAccountId, mockNewInsuranceInformation, newRegistrationForm.getNote());
        verify(registrationFormService, times(1)).createRegistrationForm(mockNewInsuredPerson, mockUserAccountId, mockNewInsuranceInformation, newRegistrationForm.getNote());

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(token, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Created registration form successfully", "Created");
    }

    @Test
    public void createRegistrationForm_WithInvalidToken_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String invalidToken = "InvalidToken";
        NewRegistrationForm newRegistrationForm = mock(NewRegistrationForm.class);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(invalidToken, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    public void createRegistrationForm_WhenUserDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        String validToken = "Bearer mockToken";
        NewRegistrationForm newRegistrationForm = mock(NewRegistrationForm.class);

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(validToken, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");
    }

    @Test
    public void createRegistrationForm_WithInvalidHealthInformation_ShouldReturnBadRequestResponse() {
        // Arrange
        String validToken = "Bearer mockToken";
        NewRegistrationForm newRegistrationForm = mock(NewRegistrationForm.class);
        HealthInformation healthInformation = mock(HealthInformation.class);

        when(newRegistrationForm.getInsuredPerson()).thenReturn(mock(InsuredPerson.class));
        when(newRegistrationForm.getHealthInformation()).thenReturn(healthInformation);
        when(newRegistrationForm.getInsuranceInformation()).thenReturn(mock(InsuranceInformation.class));

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(mock(UserAccount.class)));

        InsuranceInformation mockNewInsuranceInformation = mock(InsuranceInformation.class);
        when(insuranceInformationService.createInsuranceInformation(any())).thenReturn(mockNewInsuranceInformation);
        when(mockNewInsuranceInformation.getId()).thenReturn(1L);

        HealthInformation mockNewHealthInformation = mock(HealthInformation.class);
        when(healthInformationService.createHealthInformation(any())).thenReturn(mockNewHealthInformation);
        when(mockNewHealthInformation.getId()).thenReturn(-1L);

        InsuredPerson mockNewInsuredPerson = mock(InsuredPerson.class);
        when(insuredPersonService.createInsuredPerson(any())).thenReturn(mockNewInsuredPerson);
        when(mockNewInsuredPerson.getId()).thenReturn(1L);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(validToken, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Cannot create health information");
    }

    @Test
    public void createRegistrationForm_WithInvalidInsuranceInformation_ShouldReturnBadRequestResponse() {
        // Arrange
        String validToken = "Bearer mockToken";
        NewRegistrationForm newRegistrationForm = mock(NewRegistrationForm.class);
        HealthInformation healthInformation = mock(HealthInformation.class);

        when(newRegistrationForm.getInsuredPerson()).thenReturn(mock(InsuredPerson.class));
        when(newRegistrationForm.getHealthInformation()).thenReturn(healthInformation);
        when(newRegistrationForm.getInsuranceInformation()).thenReturn(mock(InsuranceInformation.class));

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(mock(UserAccount.class)));

        InsuranceInformation mockNewInsuranceInformation = mock(InsuranceInformation.class);
        when(insuranceInformationService.createInsuranceInformation(any())).thenReturn(mockNewInsuranceInformation);
        when(mockNewInsuranceInformation.getId()).thenReturn(-1L);

        HealthInformation mockNewHealthInformation = mock(HealthInformation.class);
        when(healthInformationService.createHealthInformation(any())).thenReturn(mockNewHealthInformation);
        when(mockNewHealthInformation.getId()).thenReturn(1L);

        InsuredPerson mockNewInsuredPerson = mock(InsuredPerson.class);
        when(insuredPersonService.createInsuredPerson(any())).thenReturn(mockNewInsuredPerson);
        when(mockNewInsuredPerson.getId()).thenReturn(1L);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(validToken, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Cannot create registration form");
    }

    @Test
    public void createRegistrationForm_WithInvalidInsuredPerson_ShouldReturnBadRequestResponse() {
        // Arrange
        String validToken = "Bearer mockToken";
        NewRegistrationForm newRegistrationForm = mock(NewRegistrationForm.class);
        HealthInformation healthInformation = mock(HealthInformation.class);

        when(newRegistrationForm.getInsuredPerson()).thenReturn(mock(InsuredPerson.class));
        when(newRegistrationForm.getHealthInformation()).thenReturn(healthInformation);
        when(newRegistrationForm.getInsuranceInformation()).thenReturn(mock(InsuranceInformation.class));

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(mock(UserAccount.class)));

        InsuranceInformation mockNewInsuranceInformation = mock(InsuranceInformation.class);
        when(insuranceInformationService.createInsuranceInformation(any())).thenReturn(mockNewInsuranceInformation);
        when(mockNewInsuranceInformation.getId()).thenReturn(1L);

        HealthInformation mockNewHealthInformation = mock(HealthInformation.class);
        when(healthInformationService.createHealthInformation(any())).thenReturn(mockNewHealthInformation);
        when(mockNewHealthInformation.getId()).thenReturn(1L);

        InsuredPerson mockNewInsuredPerson = mock(InsuredPerson.class);
        when(insuredPersonService.createInsuredPerson(any())).thenReturn(mockNewInsuredPerson);
        when(mockNewInsuredPerson.getId()).thenReturn(-1L);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(validToken, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Cannot create registration form");
    }

    @Test
    public void getRegistrationFormById_ShouldReturnRegistrationForm() {
        // Arrange
        Long userId = 123L;
        RegistrationForm mockRegistrationForm = new RegistrationForm();
        when(registrationFormService.getRegistrationFormById(userId)).thenReturn(mockRegistrationForm);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getRegistrationFormById(userId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockRegistrationForm);

        // Verify that the service method was called with the correct argument
        verify(registrationFormService, times(1)).getRegistrationFormById(userId);
    }

    @Test
    public void getRegistrationFormByUser_WhenInvalidToken_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String invalidToken = "InvalidToken";

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getRegistrationFormByUser(invalidToken);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    public void getRegistrationFormByUser_WhenUserDoesNotExist_ShouldReturnNotFoundResponse() {
        // Arrange
        String validToken = "Bearer mockToken";

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getRegistrationFormByUser(validToken);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"EmailNotFound","Could not find the user corresponding to the email");

    }

    @Test
    public void getRegistrationFormByUser_WithValidInput_ShouldReturnSuccessResponse() {
        // Arrange
        String token = "Bearer mockToken";

        when(jwtService.extractUsername(anyString())).thenReturn("mockEmail");
        UserAccount mockUserAccount = mock(UserAccount.class);
        when(userAccountService.getUserByEmail(anyString())).thenReturn(Optional.of(mockUserAccount));

        Long mockUserAccountId = 1L;
        when(mockUserAccount.getId()).thenReturn(mockUserAccountId);

        List<RegistrationForm> mockRegistrationForms = Collections.singletonList(new RegistrationForm());
        when(registrationFormService.getRegistrationFormByUserAccountId(mockUserAccountId)).thenReturn(mockRegistrationForms);
        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getRegistrationFormByUser(token);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockRegistrationForms);
    }

    @Test
    public void getAllRegistrationForm_ShouldReturnRegistrationForms() {
        // Arrange
        int page = 0;
        int size = 10;

        List<RegistrationForm> mockRegistrationForms = Collections.singletonList(new RegistrationForm());
        Page<RegistrationForm> mockPage = new PageImpl<>(mockRegistrationForms);

        when(registrationFormService.getAllRegistrationForm(any(Pageable.class))).thenReturn(mockPage);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getAllRegistrationForm(page, size);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockPage);

        // Verify
        verify(registrationFormService, times(1)).getAllRegistrationForm(any(Pageable.class));
    }

    @Test
    public void approveRegistrationForm_WhenRegistrationFormExists_ShouldReturnSuccessResponse() {
        //Arrange
        RegistrationFormController mockRegistrationFormController = mock(RegistrationFormController.class);
        Long id = 1L;
        RegistrationForm mockRegistrationForm = mock(RegistrationForm.class);

        when(registrationFormService.updateStatusRegistrationForm(id,"approved")).thenReturn(mockRegistrationForm);

        when(mockRegistrationForm.getInsuredPerson()).thenReturn(mock(InsuredPerson.class));
        when(mockRegistrationForm.getInsuredPerson().getName()).thenReturn("name");

        mockRegistrationFormController.sendResponseEmail(mockRegistrationForm, "subject", "content");
        verify(mockRegistrationFormController).sendResponseEmail(mockRegistrationForm, "subject", "content");

        insurancePaymentService.createInsurancePayment(mockRegistrationForm);
        verify(insurancePaymentService).createInsurancePayment(mockRegistrationForm);

        //Act
        ResponseEntity<?> responseEntity = registrationFormController.approveRegistrationForm(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Approved registration form successfully", "Approved");
    }

    @Test
    public void approveRegistrationForm_WhenRegistrationFormDoesNotExist_ShouldReturnBadRequestResponse() {
        //Arrange
        Long id = 1L;
        when(registrationFormService.updateStatusRegistrationForm(id,"approved")).thenReturn(null);

        //Act
        ResponseEntity<?> responseEntity = registrationFormController.approveRegistrationForm(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"CannotApproved","Cannot approve registration form");
    }

    @Test
    public void refuseRegistrationForm_WhenRegistrationFormExists_ShouldReturnSuccessResponse() {
        //Arrange
        RegistrationFormController mockRegistrationFormController = mock(RegistrationFormController.class);
        Long id = 1L;
        RegistrationForm mockRegistrationForm = mock(RegistrationForm.class);

        when(registrationFormService.updateStatusRegistrationForm(id,"refused")).thenReturn(mockRegistrationForm);

        when(mockRegistrationForm.getInsuredPerson()).thenReturn(mock(InsuredPerson.class));
        when(mockRegistrationForm.getInsuredPerson().getName()).thenReturn("name");

        mockRegistrationFormController.sendResponseEmail(mockRegistrationForm, "subject", "content");
        verify(mockRegistrationFormController).sendResponseEmail(mockRegistrationForm, "subject", "content");

        //Act
        ResponseEntity<?> responseEntity = registrationFormController.refuseRegistrationForm(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomSuccessResponse.class)
                .extracting("message", "key")
                .containsExactly("Refuse registration form successfully", "Refused");
    }

    @Test
    public void refuseRegistrationForm_WhenRegistrationFormDoesNotExist_ShouldReturnBadRequestResponse() {
        //Arrange
        Long id = 1L;
        when(registrationFormService.updateStatusRegistrationForm(id,"refused")).thenReturn(null);

        //Act
        ResponseEntity<?> responseEntity = registrationFormController.refuseRegistrationForm(id);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.BAD_REQUEST.value(),"CannotRefuse","Cannot refuse registration form");
    }

    @Test
    void sendResponseEmail_ShouldHandleException() throws MessagingException {
        // Arrange
        RegistrationForm mockRegistrationForm = mock(RegistrationForm.class);

        when(mockRegistrationForm.getInsuredPerson()).thenReturn(mock(InsuredPerson.class));
        when(mockRegistrationForm.getInsuredPerson().getEmail()).thenReturn("mockEmail");
        when(mockRegistrationForm.getInsuredPerson().getName()).thenReturn("name");

        doThrow(new RuntimeException("Error sending email")).when(myEmailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // Act
        registrationFormController.sendResponseEmail(mockRegistrationForm, "", "");

        // Assert
        // Verify that the exception is printed to the console
    }
}