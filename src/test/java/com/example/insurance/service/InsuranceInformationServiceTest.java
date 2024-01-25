package com.example.insurance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.insurance.entity.InsuranceInformation;
import com.example.insurance.repository.InsuranceInformationRepository;

public class InsuranceInformationServiceTest {

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private InsuranceInformationRepository insuranceInformationRepository;

    @InjectMocks
    private InsuranceInformationService insuranceInformationService;

    @Test
    void testCreateInsuranceInformation_WithValidInput_ReturnInsuranceInformation() {
        //Arrange
        InsuranceInformation insuranceInformation = mock(InsuranceInformation.class);

        when(insuranceInformationRepository.save(any(InsuranceInformation.class))).thenReturn(insuranceInformation);
        //Action
        InsuranceInformation actualInsuranceInformation = insuranceInformationService.createInsuranceInformation(insuranceInformation);
        //Assert
        assertThat(actualInsuranceInformation).isNotNull();
        assertThat(actualInsuranceInformation).isEqualTo(insuranceInformation);

        //Verify
        verify(insuranceInformationRepository, times(1)).save(any(InsuranceInformation.class));
    }
}
