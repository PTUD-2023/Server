package com.example.insurance.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.example.insurance.common.MapEntityToDTO;
import com.example.insurance.dto.InsurancePlanDTO;
import com.example.insurance.entity.InsurancePlan;
import com.example.insurance.repository.InsurancePlanRepository;

public class InsurancePlanServiceTest {

    @Mock
    private InsurancePlanRepository insurancePlanRepository;

    @InjectMocks
    private InsurancePlanService insurancePlanService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInsurancePlanById_WithValidInput_ReturnInsurancePlanDTO() {
        //Arrange
        InsurancePlan insurancePlan = mock(InsurancePlan.class);
        insurancePlan.setId(1L);
        MapEntityToDTO mapEntityToDTO = MapEntityToDTO.getInstance();


        when(insurancePlanRepository.findByIdAndStatus(anyLong(), anyString())).thenReturn(Optional.of(insurancePlan));

        //Action
        InsurancePlanDTO insurancePlanDTO = insurancePlanService.getInsurancePlanById(1L, "activated");

        //Assert
        assertThat(insurancePlanDTO.getId()).isEqualTo(insurancePlan.getId());
        assertThat(insurancePlanDTO).isNotNull();

        //Verify
        verify(insurancePlanRepository, times(1)).findByIdAndStatus(anyLong(), anyString());
    }

    @Test
    void testGetInsurancePlanById_WithInvalidId_ReturnNull() {
        //Arrange
        InsurancePlan insurancePlan = mock(InsurancePlan.class);
        insurancePlan.setId(-1L);

        when(insurancePlanRepository.findByIdAndStatus(anyLong(), anyString())).thenReturn(Optional.empty());

        //Action
        InsurancePlanDTO insurancePlanDTO = insurancePlanService.getInsurancePlanById(1L, "activated");

        //Assert
        assertThat(insurancePlanDTO).isNull();

        //Verify
        verify(insurancePlanRepository, times(1)).findByIdAndStatus(anyLong(), anyString());
    }

    @Test
    void testGetAllActivated_WithValidInput_ReturnListOfInsurancePlanDTO() {
        //Arange
        List<InsurancePlan> mockList = Collections.singletonList(new InsurancePlan());
        List<InsurancePlanDTO> expectedList = MapEntityToDTO.getInstance().mapInsurancePlanListToDTOList(mockList);

        when(insurancePlanRepository.findAllByStatus(anyString())).thenReturn(mockList);
        //Action
        List<InsurancePlanDTO> actualList = insurancePlanService.getAllActivated();

        //Assert
        assertThat(actualList.size()).isEqualTo(expectedList.size());

        //Verify
        verify(insurancePlanRepository, times(1)).findAllByStatus("activated");
    }
    
}
