package com.payment.UserServiceApplication.services;

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.UserServiceApplication.dto.UserRequest;
import com.payment.UserServiceApplication.models.User;
import com.payment.UserServiceApplication.repositories.UserRepository;
import com.payment.Utilities.Constants;

@Service
public class UserService implements UserDetailsService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.Authority}")
    private String userAuthority;

    public User addUser(UserRequest userRequest) throws JsonProcessingException {
        User user = userRequest.toUser();
        user.setAuthorities(userAuthority);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user = userRepository.save(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.USER_CONTACT, user.getContact());
        jsonObject.put(Constants.USER_EMAIL, user.getEmail());
        jsonObject.put(Constants.USER_ID, user.getIndentifier());
        jsonObject.put(Constants.USER_IDENTIFIER_VAUE, user.getIdentifierValue());
        jsonObject.put(Constants.USER_ID, user.getId());
        jsonObject.put(Constants.USER_NAME, user.getName());

        logger.info("json object as string" + jsonObject);
        logger.info("josn object as string mappper " + objectMapper.writeValueAsString(jsonObject));

        kafkaTemplate.send(Constants.USER_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return user;
    }

    @Override
    public User loadUserByUsername(String contact) throws UsernameNotFoundException {
        User user = userRepository.findByContact(contact);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with contact: " + contact);
        }
        return user;
    }

}
