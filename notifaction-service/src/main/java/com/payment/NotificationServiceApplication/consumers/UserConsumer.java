package com.payment.NotificationServiceApplication.consumers;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.Utilities.Constants;

@Service
public class UserConsumer {

    private final static Logger logger = LoggerFactory.getLogger(UserConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpleMailMessage simpleMailMessage;

    @Autowired
    private JavaMailSender sender;

    @KafkaListener(topics = { Constants.USER_TOPIC }, groupId = "notification-group")
    public void sendNotification(String message) throws JsonProcessingException {
        logger.info("message is " + message);
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
        logger.info("json object is " + jsonObject);
        String name = (String) jsonObject.get(Constants.USER_NAME);
        String email = (String) jsonObject.get(Constants.USER_EMAIL);

        logger.info("email and name " + email + " " + name);

        // send mail
        try {
            simpleMailMessage.setTo(email);
            simpleMailMessage.setText("Welcome " + name + " to the platform!");
            simpleMailMessage.setSubject("User created | " + name);

            sender.send(simpleMailMessage);
            logger.info("mail sent to user " + email);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("failed to send the mail", e);
        }

    }

}
