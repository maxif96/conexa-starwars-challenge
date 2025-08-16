package com.starwars.service;

import com.starwars.dto.authentication.RegisterRequest;
import com.starwars.entity.User;
import com.starwars.repository.UserRepository;
import com.starwars.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * @param registerRequest Datos del usuario a registrar
     * @return Token JWT generado para el usuario
     * @throws IllegalArgumentException Si las contraseñas no coinciden o el usuario ya existe
     */
    public String registerUser(RegisterRequest registerRequest) {
        log.info("Intentando registrar usuario: {}", registerRequest.getUsername());

        // Validar que las contraseñas coincidan
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Verificar que el usuario no exista
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        // Crear y guardar el usuario
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(newUser);
        log.info("Usuario registrado exitosamente: {}", savedUser.getUsername());

        // Generar token JWT
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getUsername(), 
                savedUser.getPassword(), 
                new ArrayList<>()
        );
        
        return jwtUtil.generateToken(userDetails);
    }

    /**
     * Verifica si un nombre de usuario está disponible
     * @param username Nombre de usuario a verificar
     * @return true si está disponible, false si ya existe
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.findByUsername(username).isPresent();
    }
}
