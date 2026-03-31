package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@test-51ndgwv8rvqlzqx8.mlsender.net");
        message.setTo(to);
        message.setSubject("E-voting: Verification code");
        message.setText(
                "Your verification code" + " " + code +
                        "\nThe code is valid through 15 minutes."
        );
        javaMailSender.send(message);
    }
}
