package com.example.insurance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyGeneralReport {
    private GeneralReportDTO current;
    private GeneralReportDTO prev;
}
