package com.example.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PackageReportDTO {
    private String packageName;
    private Long contractCount;
}
