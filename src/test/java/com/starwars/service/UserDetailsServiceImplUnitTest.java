package com.starwars.service;

import com.starwars.entity.User;
import com.starwars.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

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

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_NonExistentUser_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        userDetailsService.loadUserByUsername(username);

        // Assert - Se espera UsernameNotFoundException
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
            assertEquals("User not found with the name: nonexistent", e.getMessage());
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
            assertEquals("User not found with the name: ", e.getMessage());
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
            assertEquals("User not found with the name: null", e.getMessage());
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