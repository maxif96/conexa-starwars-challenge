package com.starwars.auth.controller;

import com.starwars.auth.dto.AuthenticationRequest;
import com.starwars.auth.dto.AuthenticationResponse;
import com.starwars.auth.dto.RegisterRequest;
import com.starwars.auth.dto.RegisterResponse;
import com.starwars.auth.service.UserDetailsServiceImpl;
import com.starwars.auth.service.UserService;
import com.starwars.shared.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "A. Authentication")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario existente y devuelve un token JWT")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw e;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema y devuelve un token JWT")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String token = userService.registerUser(registerRequest);
            RegisterResponse response = new RegisterResponse(
                "Usuario registrado exitosamente",
                registerRequest.getUsername(),
                token
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error en el registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new RegisterResponse(e.getMessage(), registerRequest.getUsername()));
        }
    }

    @GetMapping("/check-username/{username}")
    @Operation(summary = "Verificar disponibilidad de username", description = "Verifica si un nombre de usuario está disponible para registro")
    public ResponseEntity<Boolean> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }
}

