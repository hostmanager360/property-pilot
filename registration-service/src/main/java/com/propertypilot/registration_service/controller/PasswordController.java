package com.propertypilot.registration_service.controller;

import com.propertypilot.registration_service.exception.TokenExpiredException;
import com.propertypilot.registration_service.exception.TokenNotFoundException;
import com.propertypilot.registration_service.model.ForgotPasswordRequestDTO;
import com.propertypilot.registration_service.model.ResetPasswordRequestDTO;
import com.propertypilot.registration_service.model.ResponseHandler;
import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PasswordController {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseHandler<Void>> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO dto) {

        userService.forgotPassword(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Email per reset password inviata")
        );
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ResponseHandler<Void>> resetPassword(
            @RequestBody ResetPasswordRequestDTO dto) {

        userService.resetPassword(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Password reimpostata correttamente")
        );
    }
    @GetMapping("/resetPasswordVerifyToken")
    public ResponseEntity<ResponseHandler<String>> validateResetToken(@RequestParam String token) {

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token non valido"));

        if (user.getResetPasswordExpiresAt() == null ||
                user.getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token scaduto");
        }

        return ResponseEntity.ok(
                ResponseHandler.success("OK", "Token valido")
        );
    }

}