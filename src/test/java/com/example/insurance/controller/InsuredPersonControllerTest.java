package com.example.insurance.controller;

import com.example.insurance.dto.NewInsuredPerson;
import com.example.insurance.entity.HealthInformation;
import com.example.insurance.entity.InsuredPerson;
import com.example.insurance.service.HealthInformationService;
import com.example.insurance.service.InsuredPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InsuredPersonControllerTest {
    @Mock
    private InsuredPersonService insuredPersonService;
    @Mock
    private HealthInformationService healthInformationService;
    @InjectMocks
    private InsuredPersonController insuredPersonController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInsuredPerson_WithValidInput_ShouldReturnSuccessResponse() {
        // Arrange
        NewInsuredPerson newInsuredPerson = mock(NewInsuredPerson.class);
        InsuredPerson insuredPerson = mock(InsuredPerson.class);
        HealthInformation healthInformation = mock(HealthInformation.class);
        HealthInformation newHealthInformation = mock(HealthInformation.class);

        when(newInsuredPerson.getInsuredPerson()).thenReturn(insuredPerson);
        when(newInsuredPerson.getHealthInformation()).thenReturn(healthInformation);
        when(healthInformationService.createHealthInformation(any())).thenReturn(newHealthInformation);
        when(newHealthInformation.getId()).thenReturn(1L);

        when(insuredPersonService.createInsuredPerson(any())).thenReturn(insuredPerson);
        when(insuredPerson.getId()).thenReturn(1L);

        // Act
        ResponseEntity<?> responseEntity = insuredPersonController.createInsuredPerson(newInsuredPerson);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo("Create insured person successfully");

        // Verify
        verify(insuredPerson).setHealthInformation(any());
    }

    @Test
    void createInsuredPerson_WithInvalidHealthInformation_ShouldReturnBadRequestResponse() {
        // Arrange
        NewInsuredPerson newInsuredPerson = mock(NewInsuredPerson.class);
        InsuredPerson insuredPerson = mock(InsuredPerson.class);
        HealthInformation healthInformation = mock(HealthInformation.class);
        HealthInformation newHealthInformation = mock(HealthInformation.class);

        when(newInsuredPerson.getInsuredPerson()).thenReturn(insuredPerson);
        when(newInsuredPerson.getHealthInformation()).thenReturn(healthInformation);
        when(healthInformationService.createHealthInformation(any())).thenReturn(newHealthInformation);
        when(newHealthInformation.getId()).thenReturn(-1L);

        // Act
        ResponseEntity<?> responseEntity = insuredPersonController.createInsuredPerson(newInsuredPerson);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Cannot create health information");

    }

    @Test
    void createInsuredPerson_WithInvalidInsuredPerson_ShouldReturnBadRequestResponse() {
        // Arrange
        NewInsuredPerson newInsuredPerson = mock(NewInsuredPerson.class);
        InsuredPerson insuredPerson = mock(InsuredPerson.class);
        HealthInformation healthInformation = mock(HealthInformation.class);
        HealthInformation newHealthInformation = mock(HealthInformation.class);

        when(newInsuredPerson.getInsuredPerson()).thenReturn(insuredPerson);
        when(newInsuredPerson.getHealthInformation()).thenReturn(healthInformation);
        when(healthInformationService.createHealthInformation(any())).thenReturn(newHealthInformation);
        when(newHealthInformation.getId()).thenReturn(1L);

        when(insuredPersonService.createInsuredPerson(any())).thenReturn(insuredPerson);
        when(insuredPerson.getId()).thenReturn(-1L);

        // Act
        ResponseEntity<?> responseEntity = insuredPersonController.createInsuredPerson(newInsuredPerson);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Cannot create insured person");

        // Verify
        verify(insuredPerson).setHealthInformation(any());
    }

    @Test
    void getInsuredPerson_ShouldReturnInsuredPerson() {
        // Arrange
        InsuredPerson insuredPerson = mock(InsuredPerson.class);
        when(insuredPersonService.getInsuredPerson(anyLong())).thenReturn(insuredPerson);

        // Act
        ResponseEntity<?> responseEntity = insuredPersonController.getInsuredPerson(anyLong());

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(insuredPerson);
    }

    @Test
    void getInsuredPerson_WithInsuredPersonDoesNotExist_ShouldReturnBadRequestResponse() {
        // Arrange
        when(insuredPersonService.getInsuredPerson(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = insuredPersonController.getInsuredPerson(anyLong());

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Cannot find insured person");
    }
}