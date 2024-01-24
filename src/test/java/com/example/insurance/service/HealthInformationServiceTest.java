package com.example.insurance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.insurance.entity.HealthInformation;
import com.example.insurance.repository.HealthInformationRepository;

public class HealthInformationServiceTest {

    @Mock
    private HealthInformationRepository healthInformationRepository;

    @InjectMocks
    private HealthInformationService healthInformationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateHealthInformation_ReturnHealthInformation() {
        //Arrange
        HealthInformation mockHealthInformation = mock(HealthInformation.class);

        when(healthInformationRepository.save(any(HealthInformation.class))).thenReturn(mockHealthInformation);

        //Action
        HealthInformation savedHealthInformation = healthInformationService.createHealthInformation(mockHealthInformation);

        //Assert
        assertThat(savedHealthInformation).isNotNull();
        assertThat(savedHealthInformation).isEqualTo(mockHealthInformation);

        //Verify
        verify(healthInformationRepository, times(1)).save(any(HealthInformation.class));
    }

    @Test
    void testGetHealthInformationById_WithValidId_ShouldReturnHealthInformation() {
        //Arrange
        HealthInformation mockHealthInformation = mock(HealthInformation.class);
        mockHealthInformation.setId(1L);

        when(healthInformationRepository.findById(anyLong())).thenReturn(Optional.of(mockHealthInformation));

        //Action
        HealthInformation foundHealthInformation = healthInformationService.getHealthInformationById(1L);

        //Assert
        assertThat(foundHealthInformation).isNotNull();
        assertThat(foundHealthInformation).isEqualTo(mockHealthInformation);

        //Verify
        verify(healthInformationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetHealthInformationById_WithInvalidId_ShouldReturnNull() {
        //Arrange
        HealthInformation mockHealthInformation = mock(HealthInformation.class);

        when(healthInformationRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Action
        HealthInformation foundHealthInformation = healthInformationService.getHealthInformationById(-1L);

        //Assert
        assertThat(foundHealthInformation).isNull();

        //Verify
        verify(healthInformationRepository, times(1)).findById(-1L);
    }
    
}
