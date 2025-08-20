package com.starwars.service;

import com.starwars.auth.entity.User;
import com.starwars.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private com.starwars.auth.service.UserDetailsServiceImpl userDetailsService;

    @Test
    public void loadUserByUsername_ExistingUser_ShouldReturnUserDetails() {
        // Arrange
        String username = "testuser";
        String password = "encodedPassword123";
        User user = new User(username, password);
        user.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());

        verify(userRepository).findByUsername(username);
    }

    @Test
    public void loadUserByUsername_NonExistentUser_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    public void loadUserByUsername_NonExistentUser_ShouldHaveCorrectExceptionMessage() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            userDetailsService.loadUserByUsername(username);
            fail("Expected UsernameNotFoundException to be thrown");
        } catch (UsernameNotFoundException e) {
            assertEquals("Usuario no encontrado con el nombre: nonexistent", e.getMessage());
        }

        verify(userRepository).findByUsername(username);
    }

    @Test
    public void loadUserByUsername_EmptyUsername_ShouldSearchForEmptyString() {
        // Arrange
        String username = "";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            userDetailsService.loadUserByUsername(username);
            fail("Expected UsernameNotFoundException to be thrown");
        } catch (UsernameNotFoundException e) {
            assertEquals("Usuario no encontrado con el nombre: ", e.getMessage());
        }

        verify(userRepository).findByUsername("");
    }

    @Test
    public void loadUserByUsername_NullUsername_ShouldSearchForNull() {
        // Arrange
        String username = null;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            userDetailsService.loadUserByUsername(username);
            fail("Expected UsernameNotFoundException to be thrown");
        } catch (UsernameNotFoundException e) {
            assertEquals("Usuario no encontrado con el nombre: null", e.getMessage());
        }

        verify(userRepository).findByUsername(null);
    }

    @Test
    public void loadUserByUsername_ExistingUser_ShouldReturnUserDetailsWithCorrectType() {
        // Arrange
        String username = "testuser";
        String password = "encodedPassword123";
        User user = new User(username, password);
        user.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertTrue(result instanceof org.springframework.security.core.userdetails.User);
        assertEquals(0, result.getAuthorities().size());
    }

    @Test
    public void loadUserByUsername_UserWithSpecialCharacters_ShouldHandleCorrectly() {
        // Arrange
        String username = "user@domain.com";
        String password = "encodedPassword123";
        User user = new User(username, password);
        user.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());

        verify(userRepository).findByUsername(username);
    }
}