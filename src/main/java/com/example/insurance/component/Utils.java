package com.example.insurance.component;

import com.example.insurance.common.ReadEmailTemplate;
import com.example.insurance.dto.CompensationReportDTO;
import com.example.insurance.dto.IncomeReportDTO;
import com.example.insurance.dto.PaymentReportDTO;
import com.example.insurance.service.MyEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
public class Utils {
    private final MyEmailService myEmailService;

    @Autowired
    public Utils(MyEmailService myEmailService) {
        this.myEmailService = myEmailService;
    }

    public String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    public void sendResponseEmail(String to,String subject,String content,String pathTemplate,String name)
    {
        StringBuilder body = new StringBuilder();
        Resource resource = new ClassPathResource(pathTemplate);
        String htmlContent = ReadEmailTemplate.read(resource);
        htmlContent = htmlContent.replace("${lastName}",name);
        htmlContent = htmlContent.replace("${content}",content);
        body.append(htmlContent);
        new Thread(()->{
            try
            {
                myEmailService.sendSimpleMessage(to,subject,body.toString());
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public static List<IncomeReportDTO> mergeLists(List<CompensationReportDTO> compensationAmountList,
                                                    List<PaymentReportDTO> totalAmountList) {
        Map<String, IncomeReportDTO> mergedMap = new HashMap<>();

        // Populate the merged map with CompensationAmountDTO data
        for (CompensationReportDTO compensationAmountDTO : compensationAmountList) {
            String key = getKey(compensationAmountDTO.getMonth(), compensationAmountDTO.getYear());
            IncomeReportDTO mergedDTO = new IncomeReportDTO(compensationAmountDTO.getMonth(),
                    compensationAmountDTO.getYear(),
                    compensationAmountDTO.getCompensationAmount(),
                    0L);
            mergedMap.put(key, mergedDTO);
        }

        // Populate the merged map with TotalAmountDTO data
        for (PaymentReportDTO totalAmountDTO : totalAmountList) {
            String key = getKey(totalAmountDTO.getMonth(), totalAmountDTO.getYear());
            IncomeReportDTO mergedDTO = mergedMap.getOrDefault(key, new IncomeReportDTO(totalAmountDTO.getMonth(),
                    totalAmountDTO.getYear(),
                    0L,
                    totalAmountDTO.getTotalAmount()));
            mergedDTO.setTotalAmount(totalAmountDTO.getTotalAmount());
            mergedMap.put(key, mergedDTO);
        }

        // Convert the map values to a list
        return new ArrayList<>(mergedMap.values());
    }

    private static String getKey(int month, int year) {
        return month + "-" + year;
    }
}


