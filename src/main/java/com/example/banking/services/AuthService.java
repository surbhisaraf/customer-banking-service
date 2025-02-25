package com.example.banking.services;

import com.example.banking.payload.request.LoginRequest;
import com.example.banking.payload.response.JwtResponse;
import com.example.banking.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authManager;

    public JwtResponse authenticateUser(LoginRequest loginRequest) throws BadCredentialsException {

        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            Authentication auth = authManager.authenticate(authInputToken);

            String jwt = jwtUtils.generateJwtToken(auth);

            UserDetails user = (UserDetails) auth.getPrincipal();

            String role = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Role not found for the user"));

            return new JwtResponse(jwt, user.getUsername(), role);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

}
