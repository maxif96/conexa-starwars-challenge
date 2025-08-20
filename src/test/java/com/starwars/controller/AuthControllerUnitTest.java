package com.starwars.controller;

import com.starwars.auth.dto.AuthenticationRequest;
import com.starwars.auth.dto.AuthenticationResponse;
import com.starwars.auth.dto.RegisterRequest;
import com.starwars.auth.dto.RegisterResponse;
import com.starwars.shared.security.JwtUtil;
import com.starwars.auth.service.UserDetailsServiceImpl;
import com.starwars.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private com.starwars.auth.controller.AuthController authController;

    @Test
    public void createAuthenticationToken_ValidCredentials_ShouldReturnJwtToken() {
        // Arrange
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        UserDetails userDetails = new User("testuser", "encodedPassword", new ArrayList<>());
        String expectedJwt = "mock.jwt.token";

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(expectedJwt);

        // Act
        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthenticationResponse);
        AuthenticationResponse authResponse = (AuthenticationResponse) response.getBody();
        assertEquals(expectedJwt, authResponse.getJwt());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    public void createAuthenticationToken_InvalidCredentials_ShouldThrowBadCredentialsException() {
        // Arrange
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.createAuthenticationToken(authRequest);
        });
    }

    @Test
    public void registerUser_ValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("newuser", "password123", "password123");
        String expectedToken = "mock.jwt.token";

        when(userService.registerUser(registerRequest)).thenReturn(expectedToken);

        // Act
        ResponseEntity<RegisterResponse> response = authController.registerUser(registerRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuario registrado exitosamente", response.getBody().getMessage());
        assertEquals("newuser", response.getBody().getUsername());
        assertEquals(expectedToken, response.getBody().getToken());

        verify(userService).registerUser(registerRequest);
    }

    @Test
    public void registerUser_DuplicateUsername_ShouldReturnBadRequest() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "password123", "password123");

        when(userService.registerUser(registerRequest))
                .thenThrow(new IllegalArgumentException("El nombre de usuario ya está en uso"));

        // Act
        ResponseEntity<RegisterResponse> response = authController.registerUser(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El nombre de usuario ya está en uso", response.getBody().getMessage());
        assertEquals("existinguser", response.getBody().getUsername());
        assertNull(response.getBody().getToken());

        verify(userService).registerUser(registerRequest);
    }

    @Test
    public void registerUser_PasswordMismatch_ShouldReturnBadRequest() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("newuser", "password123", "differentpassword");

        when(userService.registerUser(registerRequest))
                .thenThrow(new IllegalArgumentException("Las contraseñas no coinciden"));

        // Act
        ResponseEntity<RegisterResponse> response = authController.registerUser(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Las contraseñas no coinciden", response.getBody().getMessage());
        assertEquals("newuser", response.getBody().getUsername());
        assertNull(response.getBody().getToken());

        verify(userService).registerUser(registerRequest);
    }

    @Test
    public void checkUsernameAvailability_AvailableUsername_ShouldReturnTrue() {
        // Arrange
        String username = "availableuser";
        when(userService.isUsernameAvailable(username)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = authController.checkUsernameAvailability(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(userService).isUsernameAvailable(username);
    }

    @Test
    public void checkUsernameAvailability_TakenUsername_ShouldReturnFalse() {
        // Arrange
        String username = "takenuser";
        when(userService.isUsernameAvailable(username)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = authController.checkUsernameAvailability(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());

        verify(userService).isUsernameAvailable(username);
    }

    @Test
    public void createAuthenticationToken_AuthenticationManagerThrowsException_ShouldPropagateException() {
        // Arrange
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Authentication failed"));

        // Act & Assert
        try {
            authController.createAuthenticationToken(authRequest);
            fail("Expected BadCredentialsException to be thrown");
        } catch (BadCredentialsException e) {
            assertEquals("Authentication failed", e.getMessage());
        }

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void createAuthenticationToken_ValidCredentials_ShouldCreateCorrectAuthenticationToken() {
        // Arrange
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        UserDetails userDetails = new User("testuser", "encodedPassword", new ArrayList<>());
        String expectedJwt = "mock.jwt.token";

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(expectedJwt);

        // Act
        authController.createAuthenticationToken(authRequest);

        // Assert
        verify(authenticationManager).authenticate(argThat(auth ->
                auth instanceof UsernamePasswordAuthenticationToken &&
                        auth.getName().equals("testuser") &&
                        auth.getCredentials().equals("password123")
        ));
    }
}