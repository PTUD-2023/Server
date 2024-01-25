package com.example.insurance.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.example.insurance.dto.AgeGroupReportDTO;
import com.example.insurance.dto.GeneralReportDTO;
import com.example.insurance.dto.IncomeReportDTO;
import com.example.insurance.dto.MonthlyGeneralReport;
import com.example.insurance.dto.PackageReportDTO;
import com.example.insurance.service.ReportService;

public class ReportControllerTest {
    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCountInsuranceContract_WithValidInput_ShouldReturnOkResponse() {
        //Arrange
        LocalDate currenDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);
        int month = currenDate.getMonthValue();
        int year = currenDate.getYear();
        LocalDate previousMonth = currenDate.minusMonths(1);
        
        GeneralReportDTO mockCurrentReport = mock(GeneralReportDTO.class);
        GeneralReportDTO mockPreviousReport = mock(GeneralReportDTO.class);


        when(reportService.generalReport(month, year)).thenReturn(mockCurrentReport);
        when(reportService.generalReport(previousMonth.getMonthValue(), previousMonth.getYear())).thenReturn(mockPreviousReport);

        MonthlyGeneralReport monthlyGeneralReport = new MonthlyGeneralReport();
        monthlyGeneralReport.setCurrent(mockCurrentReport);
        monthlyGeneralReport.setPrev(mockPreviousReport);

        ResponseEntity<?> responseEntity = this.reportController.countInsuranceContract(0, 0);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);

        verify(reportService, times(1)).generalReport(month, year);
        verify(reportService, times(1)).generalReport(previousMonth.getMonthValue(), previousMonth.getYear());

    }

    @Test
    void testGetCountContractByAgeGroup_ShouldReturnOkResponse() {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        List<AgeGroupReportDTO> mockList = Collections.singletonList(new AgeGroupReportDTO());
        AgeGroupReportDTO ageGroupReportDTO = mock(AgeGroupReportDTO.class);
        when(reportService.getCountContractsByAgeGroup(anyInt(), anyInt())).thenReturn(mockList);

        //Action
        ResponseEntity<?> responseEntity = reportController.getCountContractsByAgeGroup(0, 0);

        //Assert
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockList);

        //Verify
        verify(reportService, times(1)).getCountContractsByAgeGroup(month, year);
    }

    @Test
    void testGetCountContractByPlan_ShouldReturnOkResponse() {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        PackageReportDTO mockPackageReportDTO = mock(PackageReportDTO.class);
        List<PackageReportDTO> mockList = Collections.singletonList(mockPackageReportDTO);

        when(reportService.getCountContractsByPlan(anyInt(), anyInt())).thenReturn(mockList);

        //Action
        ResponseEntity<?> responseEntity = reportController.getCountContractsByPlan(0, 0);

        //Assert
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockList);

        //Verify
        verify(reportService, times(1)).getCountContractsByPlan(month, year);
    }

    @Test
    void testTest_ShouldReturnOkResponse() {
        //Arrange
        List<IncomeReportDTO> mockList = Collections.singletonList(mock(IncomeReportDTO.class));
        
        when(reportService.test(anyInt())).thenReturn(mockList);

        //Action
        ResponseEntity<?> responseEntity = reportController.test();

        //Assert
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockList);
    }
}
