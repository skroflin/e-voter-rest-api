package com.skroflin.evoting_rest_api.service;

public interface EmailService {
    void sendVerificationEmail(String to, String code);
    void sendPasswordResetEmail(String to, String code);
}
