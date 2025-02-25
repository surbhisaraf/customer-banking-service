package com.example.banking.controllers;

import com.example.banking.payload.request.LoginRequest;
import com.example.banking.payload.response.GenericResponse;
import com.example.banking.payload.response.JwtResponse;
import com.example.banking.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.status(200).body(new GenericResponse<>("loggedin successfully", jwtResponse));
    }
}
