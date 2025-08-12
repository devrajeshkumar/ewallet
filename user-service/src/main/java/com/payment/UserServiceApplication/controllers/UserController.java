package com.payment.UserServiceApplication.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.UserServiceApplication.dto.UserRequest;
import com.payment.UserServiceApplication.models.User;
import com.payment.UserServiceApplication.services.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody @Valid UserRequest userRequest)
            throws JsonProcessingException {
        User user = userService.addUser(userRequest);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/userDetails")
    public User getUser(@RequestParam("contact") String contact) {
        return userService.loadUserByUsername(contact);
    }

}
