package com.propertypilot.registration_service.controller;

import com.propertypilot.registration_service.model.UserDto;
import com.propertypilot.registration_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto user) {
        userService.registerUser(user.getEmail(), user.getPassword(), user.getRole());
        return ResponseEntity.ok("Utente registrato. Conferma l'email per attivare l'account.");
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String email) {
        userService.enableUser(email);
        return ResponseEntity.ok("Utente attivato");
    }
}