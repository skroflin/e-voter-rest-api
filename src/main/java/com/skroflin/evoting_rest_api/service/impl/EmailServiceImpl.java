package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static com.skroflin.evoting_rest_api.util.EmailTemplateUtil.*;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@test-51ndgwv8rvqlzqx8.mlsender.net");
            helper.setTo(to);
            helper.setSubject("E-voting | Verifikacijski kod za prijavu");

            String htmlContent = buildVerificationEmailTemplate(code);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Unsuccessful e-mail verification code sent", e);
        }
    }
}
