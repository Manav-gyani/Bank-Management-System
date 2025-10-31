package com.bank.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendWelcomeEmail(String to, String username) {
        logger.info("Sending welcome email to: {}", to);
        // Implementation for sending email
        // This is a placeholder - integrate with actual email service
    }

    public void sendTransactionAlert(String to, String transactionDetails) {
        logger.info("Sending transaction alert to: {}", to);
        // Implementation for sending transaction alert
    }

    public void sendOTP(String to, String otp) {
        logger.info("Sending OTP to: {}", to);
        // Implementation for sending OTP
    }

    public void sendPasswordResetLink(String to, String resetLink) {
        logger.info("Sending password reset link to: {}", to);
        // Implementation for sending password reset link
    }
}