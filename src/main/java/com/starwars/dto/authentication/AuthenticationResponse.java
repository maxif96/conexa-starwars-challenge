package com.starwars.dto.authentication;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthenticationResponse implements Serializable {
    private final String jwt;
}