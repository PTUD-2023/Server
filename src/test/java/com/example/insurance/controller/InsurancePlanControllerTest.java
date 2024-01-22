package com.example.insurance.controller;


import com.example.insurance.common.CustomErrorResponse;
import com.example.insurance.dto.InsurancePlanDTO;
import com.example.insurance.service.InsurancePlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class InsurancePlanControllerTest {
    @Mock
    private InsurancePlanService insurancePlanService;

    @InjectMocks
    private InsurancePlanController insurancePlanController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPlanWithPrice_WithInsurancePlanDoesNotExist_ShouldReturnBadRequestResponse() {
        //Arrange
        String status = "activated";
        when(insurancePlanService.getInsurancePlanById(anyLong(), eq(status))).thenReturn(null);

        //Action
        Long id = 1L;
        ResponseEntity<?> responseEntity = insurancePlanController.getPlanWithPrices(id);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isInstanceOf(CustomErrorResponse.class)
                .extracting("statusCode", "errorKey", "message")
                .containsExactly(HttpStatus.NOT_FOUND.value(),"PlanNotFound","Cannot find insurance plan with id: " +id);
    }

    @Test
    void testGetPlanWithPrice_WithValidInsurancePlanId_ShouldReturnOkResponse() {

        //Arrange
        String status = "activated";
        InsurancePlanDTO insurancePlanDTO = mock(InsurancePlanDTO.class);
        when(insurancePlanService.getInsurancePlanById(anyLong(), eq(status))).thenReturn(insurancePlanDTO);


        //Action
        ResponseEntity<?> responseEntity = insurancePlanController.getPlanWithPrices(1L);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(insurancePlanDTO);
    }

    @Test
    void testGetAllInsurancePlan_ShouldReturnOkResponse() {
        //Arrange
        List<InsurancePlanDTO> insurancePlanDTOS = Collections.singletonList(new InsurancePlanDTO());
        when(insurancePlanService.getAllActivated()).thenReturn(insurancePlanDTOS);

        //Action
        ResponseEntity<?> responseEntity = insurancePlanController.getAllInsurancePlan();

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(insurancePlanDTOS);
    }
}
