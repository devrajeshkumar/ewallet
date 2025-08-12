package com.payment.UserServiceApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.UserServiceApplication.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByContact(String contact);
}
