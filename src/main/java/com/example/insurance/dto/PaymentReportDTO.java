package com.example.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentReportDTO {
    private int month;
    private int year;
    private Long totalAmount;
}
