package com.example.insurance.controller;

import com.example.insurance.dto.MonthlyGeneralReport;
import com.example.insurance.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/report")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }



    @GetMapping("/general")
    public ResponseEntity<?> countInsuranceContract(@RequestParam(defaultValue = "0") int month, @RequestParam(defaultValue = "0")  int year)
    {
        if(month == 0)
        {
            month = LocalDate.now().getMonthValue();
        }
        if (year == 0)
        {
            year = LocalDate.now().getYear();
        }
        LocalDate currentDate = LocalDate.of(year, month, 1);

        // Lấy tháng trước đó
        LocalDate previousMonth = currentDate.minusMonths(1);

        MonthlyGeneralReport monthlyGeneralReport = new MonthlyGeneralReport();
        monthlyGeneralReport.setCurrent(reportService.generalReport(month,year));
        monthlyGeneralReport.setPrev(reportService.generalReport(previousMonth.getMonthValue(),previousMonth.getYear()));

        return ResponseEntity.ok(monthlyGeneralReport);
    }

    @GetMapping("/age")
    public ResponseEntity<?> getCountContractsByAgeGroup(@RequestParam(defaultValue = "0") int month, @RequestParam(defaultValue = "0")  int year)
    {
        if(month == 0)
        {
            month = LocalDate.now().getMonthValue();
        }
        if (year == 0)
        {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(reportService.getCountContractsByAgeGroup(month,year));
    }

    @GetMapping("/plan")
    public ResponseEntity<?> getCountContractsByPlan(@RequestParam(defaultValue = "0") int month, @RequestParam(defaultValue = "0")  int year)
    {
        if(month == 0)
        {
            month = LocalDate.now().getMonthValue();
        }
        if (year == 0)
        {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(reportService.getCountContractsByPlan(month,year));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test()
    {
        return ResponseEntity.ok(reportService.test(2024));
    }

}
