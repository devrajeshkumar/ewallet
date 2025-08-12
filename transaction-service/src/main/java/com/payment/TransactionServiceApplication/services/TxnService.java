package com.payment.TransactionServiceApplication.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.payment.TransactionServiceApplication.repositories.TxnRepository;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.TransactionServiceApplication.models.Txn;
import com.payment.TransactionServiceApplication.models.TxnStatus;

@Service
public class TxnService implements UserDetailsService {

        private final static Logger logger = LoggerFactory.getLogger(TxnService.class);
        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private TxnRepository txnRepository;

        @Autowired
        private KafkaTemplate<String, Object> kafkaTemplate;

        @Autowired
        ObjectMapper objectMapper;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth("txn-service", "txn-service");
                HttpEntity<JSONObject> reqEntity = new HttpEntity<>(headers);
                JSONObject jsonObject = restTemplate
                                .exchange("http://localhost:8081/user/userDetails?contact=" + username, HttpMethod.GET,
                                                reqEntity,
                                                JSONObject.class)
                                .getBody();

                logger.info("user details for {} are {}", username, jsonObject);
                List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>) (Object) jsonObject
                                .get("authorities");
                List<GrantedAuthority> authorities = list.stream().map(x -> x.get("authority"))
                                .map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toList());
                User user = new User((String) jsonObject.get("username"), (String) jsonObject.get("password"),
                                authorities);
                return user;
        }

        public String initTxn(String receiver, String note, Double amount, String username)
                        throws JsonProcessingException {
                Txn txn = Txn.builder().txnId(UUID.randomUUID().toString()).amount(amount).receiver(receiver)
                                .sender(username)
                                .note(note).status(TxnStatus.INITIATED).build();
                Txn savedTxn = txnRepository.save(txn);
                HashMap<String, Object> map = new HashMap<>();

                map.put("sender", username);
                map.put("receiver", receiver);
                map.put("amount", amount);
                map.put("note", note);
                map.put("status", txn.getStatus());
                JSONObject jsonObject = new JSONObject(map);

                kafkaTemplate.send("TXN_TOPIC", objectMapper.writeValueAsString(jsonObject));
                return savedTxn.getTxnId();
        }

}
