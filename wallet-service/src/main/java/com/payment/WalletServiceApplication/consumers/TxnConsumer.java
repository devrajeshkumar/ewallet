package com.payment.WalletServiceApplication.consumers;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.WalletServiceApplication.repositories.WalletRepository;

@Service
public class TxnConsumer {

    private final static Logger logger = LoggerFactory.getLogger(TxnConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @KafkaListener(topics = { "TXN_TOPIC" }, groupId = "wallet-group")
    public void updateWallet(String message) throws JsonMappingException, JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);

        String sender = (String) jsonObject.get("sender");
        String receiver = (String) jsonObject.get("receiver");
        String note = (String) jsonObject.get("note");
        double amount = (double) jsonObject.get("amount");
        String txnId = (String) jsonObject.get("sender");

        // update sender wallet
        walletRepository.updateWallet(sender, -amount);

        // udpate receiver wallet
        walletRepository.updateWallet(receiver, amount);
        logger.info("txn is sucessful for both receiver: {} and sender: {} with txn id: {} and message: {}", receiver,
                sender, txnId, note);
    }
}
