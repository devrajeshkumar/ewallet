package com.payment.UserServiceApplication.dto;

import com.payment.UserServiceApplication.models.User;
import com.payment.UserServiceApplication.models.UserIndentifier;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserRequest {
    private String name;
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;
    @NotBlank(message = "Contact number is mandatory")
    private String contact;
    private String address;
    private String dob;
    @NotBlank(message = "Identifier type is mandatory")
    private String identifierType; // e.g., PAN, ADHAR_NO
    @NotBlank(message = "Identifier value is mandatory")
    private String identifierValue;

    public User toUser() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .contact(contact)
                .address(address)
                .dob(dob)
                .identifierValue(identifierValue)
                .indentifier(UserIndentifier.valueOf(identifierType.toUpperCase()))
                .build();
    }

}
