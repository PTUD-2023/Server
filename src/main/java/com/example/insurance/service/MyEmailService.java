package com.example.insurance.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MyEmailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public MyEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @Async
    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,"UTF-8");
            helper.setFrom("tathi123789@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text,true);
            javaMailSender.send(mimeMessage);
        }
        catch (MessagingException e)
        {
            System.out.println(e.getMessage());
            throw new MessagingException();
        }

    }
}
