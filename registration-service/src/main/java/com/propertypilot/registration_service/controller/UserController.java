package com.propertypilot.registration_service.controller;

import com.propertypilot.registration_service.model.UserDto;
import com.propertypilot.registration_service.model.VerificationToken;
import com.propertypilot.registration_service.service.UserService;
import com.propertypilot.registration_service.util.VerifyTokenRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private VerifyTokenRegistration verifyTokenRegistration;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto user) {
        userService.registerUser(user.getEmail(), user.getPassword(), user.getRole());
        return ResponseEntity.ok("Utente registrato. Conferma l'email per attivare l'account.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        verifyTokenRegistration.verifyToken(token);
        return ResponseEntity.ok("Account verificato con successo");
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String email) {
        userService.enableUser(email);
        return ResponseEntity.ok("Utente attivato");
    }
}