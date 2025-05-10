package com.seraphim.loyverse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendNotification(String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("jrdimayuga.arw@gmail.com");
        msg.setTo(new String[] {
                "jrdimayuga.arw@gmail.com",
                "karen.a.dimayuga@gmail.com"
        });
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}

