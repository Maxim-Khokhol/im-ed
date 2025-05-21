package com.proImg.image_editor;

import com.proImg.image_editor.dtos.JwtRequest;
import com.proImg.image_editor.dtos.JwtResponse;
import com.proImg.image_editor.entities.User;
import com.proImg.image_editor.service.AuthService;
import com.proImg.image_editor.service.UserService;
import com.proImg.image_editor.utils.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Test
    void createAuthToken_success() {
        // Mock all dependencies
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenUtils jwtTokenUtils = mock(JwtTokenUtils.class);
        UserService userService = mock(UserService.class);

        // Service under test
        AuthService authService = new AuthService(userService, jwtTokenUtils, authenticationManager);

        // Create auth request manually (без конструктора)
        JwtRequest request = new JwtRequest();
        request.setUsername("demo");
        request.setPassword("pass");

        // Mock authentication and token generation
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("demo");

        when(userService.loadUserByUsername("demo")).thenReturn(mockUserDetails);
        when(jwtTokenUtils.generateToken(mockUserDetails)).thenReturn("token123");

        // Call method
        ResponseEntity<?> response = authService.createAuthToken(request);

        // Assert result
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        assertEquals("token123", ((JwtResponse) response.getBody()).getToken());
    }
}

