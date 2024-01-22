package com.example.insurance.controller;


import com.example.insurance.dto.InsurancePlanPriceDTO;
import com.example.insurance.service.InsurancePlanPriceService;
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

public class InsurancePlanPriceControllerTest {

    @InjectMocks
    private InsurancePlanPriceController insurancePlanPriceController;

    @Mock
    private InsurancePlanPriceService insurancePlanPriceService;

    @BeforeEach()
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPriceForInsurancePlan_ShouldReturnOkResponse() {
        //Arrange
        String status = "activated";
        List<InsurancePlanPriceDTO> insurancePlanPriceDTOS = Collections.singletonList(new InsurancePlanPriceDTO());
        when(insurancePlanPriceService.getPricesForInsurancePlan(anyLong(), eq(status)))
                .thenReturn(insurancePlanPriceDTOS);

        //Action
        ResponseEntity<?> responseEntity = insurancePlanPriceController.getPricesForInsurancePlan(1L);

        //Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(insurancePlanPriceDTOS);

    }
}
