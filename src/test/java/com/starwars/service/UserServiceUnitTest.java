package com.starwars.service;

import com.starwars.dto.authentication.RegisterRequest;
import com.starwars.entity.User;
import com.starwars.repository.UserRepository;
import com.starwars.security.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerUser_ValidRequest_ShouldReturnJwtToken() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("newuser", "password123", "password123");
        String encodedPassword = "encodedPassword123";
        String expectedJwt = "mock.jwt.token";

        User savedUser = new User("newuser", encodedPassword);
        savedUser.setId(1L);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedJwt);

        // Act
        String result = userService.registerUser(registerRequest);

        // Assert
        assertEquals(expectedJwt, result);

        // Verificar que se guardó el usuario correctamente
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("newuser", capturedUser.getUsername());
        assertEquals(encodedPassword, capturedUser.getPassword());

        verify(userRepository).findByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerUser_PasswordMismatch_ShouldThrowException() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("newuser", "password123", "differentpassword");

        // Act
        userService.registerUser(registerRequest);

        // Assert - Se espera IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerUser_UsernameAlreadyExists_ShouldThrowException() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "password123", "password123");
        User existingUser = new User("existinguser", "encodedPassword");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act
        userService.registerUser(registerRequest);

        // Assert - Se espera IllegalArgumentException
    }

    @Test
    public void registerUser_PasswordMismatch_ShouldNotInteractWithRepository() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("newuser", "password123", "differentpassword");

        // Act & Assert
        try {
            userService.registerUser(registerRequest);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Las contraseñas no coinciden", e.getMessage());
        }

        // Verificar que no se interactuó con el repositorio ni con otros servicios
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void registerUser_UsernameAlreadyExists_ShouldNotSaveUser() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "password123", "password123");
        User existingUser = new User("existinguser", "encodedPassword");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        try {
            userService.registerUser(registerRequest);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("El nombre de usuario ya está en uso", e.getMessage());
        }

        verify(userRepository).findByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    public void isUsernameAvailable_UserDoesNotExist_ShouldReturnTrue() {
        // Arrange
        String username = "availableuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.isUsernameAvailable(username);

        // Assert
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void isUsernameAvailable_UserExists_ShouldReturnFalse() {
        // Arrange
        String username = "takenuser";
        User existingUser = new User("takenuser", "encodedPassword");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        // Act
        boolean result = userService.isUsernameAvailable(username);

        // Assert
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void registerUser_ValidRequest_ShouldCreateUserDetailsCorrectly() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password123", "password123");
        String encodedPassword = "encodedPassword123";
        String expectedJwt = "mock.jwt.token";

        User savedUser = new User("testuser", encodedPassword);
        savedUser.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(expectedJwt);

        // Act
        userService.registerUser(registerRequest);

        // Assert
        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(jwtUtil).generateToken(userDetailsCaptor.capture());

        UserDetails capturedUserDetails = userDetailsCaptor.getValue();
        assertEquals("testuser", capturedUserDetails.getUsername());
        assertEquals(encodedPassword, capturedUserDetails.getPassword());
        assertTrue(capturedUserDetails.getAuthorities().isEmpty());
    }

    @Test
    public void registerUser_OneNullPassword_ShouldThrowException() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password123", null);

        // Act & Assert
        try {
            userService.registerUser(registerRequest);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Las contraseñas no coinciden", e.getMessage());
        }
    }
}