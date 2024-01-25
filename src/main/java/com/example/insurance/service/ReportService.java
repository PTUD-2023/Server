package com.example.insurance.service;

import com.example.insurance.component.Utils;
import com.example.insurance.dto.*;
import com.example.insurance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final InsuranceContractRepository insuranceContractRepository;
    private final RegistrationFormRepository registrationFormRepository;
    private final InsurancePaymentRepository insurancePaymentRepository;
    private final InsuredPersonRepository insuredPersonRepository;
    private final ClaimRequestRepository claimRequestRepository;

    @Autowired
    public ReportService(InsuranceContractRepository insuranceContractRepository, RegistrationFormRepository registrationFormRepository, InsurancePaymentRepository insurancePaymentRepository, InsuredPersonRepository insuredPersonRepository, ClaimRequestRepository claimRequestRepository) {
        this.insuranceContractRepository = insuranceContractRepository;
        this.registrationFormRepository = registrationFormRepository;
        this.insurancePaymentRepository = insurancePaymentRepository;
        this.insuredPersonRepository = insuredPersonRepository;
        this.claimRequestRepository = claimRequestRepository;
    }

    public GeneralReportDTO generalReport(int month, int year)
    {
        GeneralReportDTO generalReportDTO = new GeneralReportDTO();
        generalReportDTO.setCountInsuranceContract(insuranceContractRepository.countInsuranceContractsByMonth(month,year));
        generalReportDTO.setCountRegistrationForm(registrationFormRepository.countRegistrationFormsByMonth(month,year));
        generalReportDTO.setRevenue(insurancePaymentRepository.getTotalRevenueByMonth(month,year));
        generalReportDTO.setCountInsuredPerson(insuredPersonRepository.countUniqueInsuredPersonsByMonth(month,year));

        return generalReportDTO;
    }

    public List<SaleReportDTO> findMostRegisteredPackageByMonth() {
        return registrationFormRepository.findMostRegisteredPackageByMonth(1,2024);
    }

    public List<AgeGroupReportDTO>  getCountContractsByAgeGroup(int month, int year ) {
        List<String> allAgeGroups = Arrays.asList("1-3 tuổi", "4-10 tuổi", "11-18 tuổi", "19-40 tuổi", "41-50 tuổi", "51-60 tuổi");
        List<AgeGroupReportDTO> result = insuranceContractRepository.countContractsByAgeGroup(month, year);

        Map<String, AgeGroupReportDTO> resultMap = result.stream()
                .collect(Collectors.toMap(AgeGroupReportDTO::getAgeGroup, Function.identity()));

        for (String ageGroup : allAgeGroups) {
            resultMap.putIfAbsent(ageGroup, new AgeGroupReportDTO(ageGroup, 0L));
        }

        result = new ArrayList<>(resultMap.values());
        return result;
    }

    public List<PackageReportDTO> getCountContractsByPlan (int month,int year)
    {
        List<String> allPlan = Arrays.asList("Essential Protection", "Standard Coverage", "Advanced Care", "Comprehensive Shield", "Premium Assurance", "Elite Safeguard");
        List<PackageReportDTO> result = insuranceContractRepository.countContractsByPlan(month, year);

        Map<String, PackageReportDTO> resultMap = result.stream()
                .collect(Collectors.toMap(PackageReportDTO::getPackageName, Function.identity()));

        for (String plan : allPlan) {
            resultMap.putIfAbsent(plan, new PackageReportDTO(plan, 0L));
        }

        result = new ArrayList<>(resultMap.values());
        return result;
    }

    public List<PaymentReportDTO> generateMonthlyPaymentReport(int year)
    {
        List<PaymentReportDTO> reportData = insurancePaymentRepository.generateMonthlyPaymentReport(year);
        List<PaymentReportDTO> completeReportData = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            completeReportData.add(new PaymentReportDTO(month, year, 0L));
        }
        for (PaymentReportDTO entry : reportData) {
            int month = entry.getMonth();
            completeReportData.set(month - 1, entry);
        }
        return completeReportData;
    }

    public List<CompensationReportDTO> generateMonthlyCompensationAmountReport(int year)
    {
        return claimRequestRepository.generateMonthlyCompensationAmountReport(year);
    }

    public List<IncomeReportDTO> getIncomeReport(int year)
    {
        List<PaymentReportDTO> paymentReportDTO = generateMonthlyPaymentReport(year);
        List<CompensationReportDTO> compensationReportDTO = generateMonthlyCompensationAmountReport(year);
        List<IncomeReportDTO> mergedList = Utils.mergeLists(compensationReportDTO,paymentReportDTO);
        mergedList.sort((dto1, dto2) -> Integer.compare(dto1.getMonth(), dto2.getMonth()));
        return mergedList;
    }

}
