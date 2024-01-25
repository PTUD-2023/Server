package com.example.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompensationReportDTO {
    private int month;
    private int year;
    private Long compensationAmount;
}
