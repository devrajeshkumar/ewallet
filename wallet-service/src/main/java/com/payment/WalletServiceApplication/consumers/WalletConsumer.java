package com.payment.WalletServiceApplication.consumers;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.Utilities.Constants;
import com.payment.WalletServiceApplication.models.Wallet;
import com.payment.WalletServiceApplication.repositories.WalletRepository;

@Service
public class WalletConsumer {

    private final static Logger logger = LoggerFactory.getLogger(WalletConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Value("${user.creation.time.balance}")
    private Double balance;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = { Constants.USER_TOPIC }, groupId = "wallet-group")
    public void createWallet(String message) throws JsonMappingException, JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);

        String contact = (String) jsonObject.get(Constants.USER_CONTACT);
        Integer userID = (Integer) jsonObject.get(Constants.USER_ID);

        Wallet wallet = Wallet.builder().userId(userID).contact(contact).balance(balance).build();
        walletRepository.save(wallet);
        logger.info("wallet created for user: {} with initial balance {}", userID, balance);

        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.USER_ID, userID);
        map.put(Constants.WALLET_BALANCE, balance);

        JSONObject json = new JSONObject(map);

        kafkaTemplate.send(Constants.WALLET_TOPIC, objectMapper.writeValueAsString(json));

        logger.info("message push to kafka queue in topic: {} with data {}", Constants.WALLET_TOPIC, json);

    }
}
