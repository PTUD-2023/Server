package com.example.insurance.component;

import com.example.insurance.common.ReadEmailTemplate;
import com.example.insurance.service.MyEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Objects;

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
}


