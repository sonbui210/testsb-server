package com.example.testsb2.api.auth.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank
    private String username ;

    @NotBlank
    private String password;
}
