package com.payment.NotificationServiceApplication.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class NotificationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SimpleMailMessage sendMailMessage() {
        return new SimpleMailMessage();
    }
}
