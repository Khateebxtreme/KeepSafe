package com.keepsafe.notes.security.request;

import lombok.Data;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Data
public class SignupRequest {
    //DTO class that determines and handles the format of request coming to register a user into the system (Register Path)
    //Some Necessary server side validation is also performed using the mentioned annotations on various fields to track some general issues like good password and username length, correct format of email entered etc in the incoming request.

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Setter
    @Getter
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
