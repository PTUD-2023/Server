package com.example.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class IncomeReportDTO {
    private int month;
    private int year;
    private Long compensationAmount;
    private Long totalAmount;
}
