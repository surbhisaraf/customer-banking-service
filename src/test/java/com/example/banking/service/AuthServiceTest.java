package com.example.banking.service;

import com.example.banking.payload.request.LoginRequest;
import com.example.banking.payload.response.JwtResponse;
import com.example.banking.security.JwtUtils;
import com.example.banking.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

        @Mock
        private AuthenticationManager authManager;

        @Mock
        private JwtUtils jwtUtils;

        @Mock
        private Authentication authentication;

        @InjectMocks
        private AuthService authService;

        private LoginRequest mockLoginRequest;
        private UserDetails mockUserDetails;
        private String mockJwtToken = "mocked-jwt-token";

        @BeforeEach
        void setUp() {
            mockLoginRequest = new LoginRequest();
            mockLoginRequest.setUsername("testUser");
            mockLoginRequest.setPassword("testPassword");

            mockUserDetails = new User("testUser", "testPassword", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        }

        @Test
        void testAuthenticateUser_Success() {
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(mockJwtToken);

            JwtResponse response = authService.authenticateUser(mockLoginRequest);

            assertNotNull(response);
            assertEquals("testUser", response.getUsername());
            assertEquals("ROLE_USER", response.getRole());
            assertEquals(mockJwtToken, response.getToken());
        }

        @Test
        void testAuthenticateUser_InvalidCredentials() {
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid username or password"));

            assertThrows(BadCredentialsException.class, () -> authService.authenticateUser(mockLoginRequest));
        }

        @Test
        void testAuthenticateUser_NoRoleFound() {
            UserDetails noRoleUser = (UserDetails) new User("testUser", "testPassword", List.of());
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(noRoleUser);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(mockJwtToken);

            Exception exception = assertThrows(RuntimeException.class, () -> authService.authenticateUser(mockLoginRequest));
            assertEquals("Role not found for the user", exception.getMessage());
        }


}
