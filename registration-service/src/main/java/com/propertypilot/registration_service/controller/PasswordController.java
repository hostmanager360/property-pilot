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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class PasswordController {


    private final UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseHandler<Void>> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO dto) {

        log.info("Richiesta forgot-password per email {}", dto.getEmail());

        userService.forgotPassword(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Email per reset password inviata")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseHandler<Void>> resetPassword(
            @RequestBody ResetPasswordRequestDTO dto) {

        log.info("Reset password richiesto per token {}", dto.getToken());

        userService.resetPassword(dto);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Password reimpostata correttamente")
        );
    }

    @GetMapping("/reset-password/verify")
    public ResponseEntity<ResponseHandler<Void>> validateResetToken(@RequestParam String token) {

        log.info("Verifica token reset-password: {}", token);

        userService.validateResetPasswordToken(token);

        return ResponseEntity.ok(
                ResponseHandler.success(null, "Token valido")
        );
    }
}
