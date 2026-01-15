package com.propertypilot.authservice.controller;

import com.propertypilot.authservice.dto.LoginRequest;
import com.propertypilot.authservice.dto.LoginResponse;
import com.propertypilot.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println("Login chiamato con username: " + request.getEmail());

        return ResponseEntity.ok(authService.login(request));
    }
}