package com.payment.UserServiceApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.payment.UserServiceApplication.models.User;
import com.payment.UserServiceApplication.models.UserType;
import com.payment.UserServiceApplication.repositories.UserRepository;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Value("${service.Authority}")
	private String serviceAuthority;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = User.builder().contact("txn-service").password(passwordEncoder.encode("txn-service"))
				.email("txn.service@gmail.com").authorities(serviceAuthority).userType(UserType.SERVICE).build();

		User existingUser = userRepository.findByContact("txn-service");
		if (existingUser == null) {
			userRepository.save(user);
		}
	}

}
