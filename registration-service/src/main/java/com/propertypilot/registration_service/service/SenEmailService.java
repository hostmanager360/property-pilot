package com.propertypilot.registration_service.service;

public interface SenEmailService {
    public void sendVerificationEmail(String to,String name, String link);

    void sendResetPasswordEmail(String email, String email1, String link);
    void sendVerificationEmailWithInitialPassword(
            String to,
            String name,
            String email,
            String initialPassword,
            String verificationLink
    );
}
