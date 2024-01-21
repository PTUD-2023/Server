package com.example.insurance.controller;

import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.common.CustomSuccessResponse;
import com.example.insurance.entity.*;
import com.example.insurance.service.*;
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
    private JwtService jwtService;

    @InjectMocks
    private RegistrationFormController registrationFormController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRegistrationForm_WithValidInput() {
        // Arrange
        String token = "Bearer mockToken"; //Token == null and token start with Bearer
        RegistrationFormController.NewRegistrationForm newRegistrationForm = mock(RegistrationFormController.NewRegistrationForm.class);
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
    public void testCreateRegistrationForm_WithInvalidToken() {
        // Arrange
        String invalidToken = "InvalidToken";
        RegistrationFormController.NewRegistrationForm newRegistrationForm = mock(RegistrationFormController.NewRegistrationForm.class);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.createRegistrationForm(invalidToken, newRegistrationForm);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    public void testCreateRegistrationForm_WithMissingUser() {
        // Arrange
        String validToken = "Bearer mockToken";
        RegistrationFormController.NewRegistrationForm newRegistrationForm = mock(RegistrationFormController.NewRegistrationForm.class);

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
    public void testCreateRegistrationForm_WithInvalidHealthInformation() {
        // Arrange
        String validToken = "Bearer mockToken";
        RegistrationFormController.NewRegistrationForm newRegistrationForm = mock(RegistrationFormController.NewRegistrationForm.class);
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
    public void testCreateRegistrationForm_WithInvalidInsuranceInformation() {
        // Arrange
        String validToken = "Bearer mockToken";
        RegistrationFormController.NewRegistrationForm newRegistrationForm = mock(RegistrationFormController.NewRegistrationForm.class);
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
    public void testCreateRegistrationForm_WithInvalidInsuredPerson() {
        // Arrange
        String validToken = "Bearer mockToken";
        RegistrationFormController.NewRegistrationForm newRegistrationForm = mock(RegistrationFormController.NewRegistrationForm.class);
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
    public void testGetRegistrationFormByUserAccountId() {
        // Arrange
        Long userId = 123L;
        RegistrationForm mockRegistrationForm = new RegistrationForm(); // Replace with your actual class
        when(registrationFormService.getRegistrationFormById(userId)).thenReturn(mockRegistrationForm);

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getRegistrationFormByUserAccountId(userId);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockRegistrationForm); // Use appropriate assertions

        // Verify that the service method was called with the correct argument
        verify(registrationFormService, times(1)).getRegistrationFormById(userId);
    }

    @Test
    void testGetRegistrationFormByUser_WithInvalidToken() {
        // Arrange
        String invalidToken = "InvalidToken";

        // Act
        ResponseEntity<?> responseEntity = registrationFormController.getRegistrationFormByUser(invalidToken);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void testGetRegistrationFormByUser_WithMissingUser() {
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
    void testGetRegistrationFormByUser_WithValidInput() {
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
    public void testGetAllRegistrationForm() {
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

        // Verify that the service method was called with the correct argument
        verify(registrationFormService, times(1)).getAllRegistrationForm(any(Pageable.class));
    }
}