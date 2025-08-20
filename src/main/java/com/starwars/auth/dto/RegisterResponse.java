package com.starwars.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private String message;
    private String username;
    private String token;

    public RegisterResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }
}

