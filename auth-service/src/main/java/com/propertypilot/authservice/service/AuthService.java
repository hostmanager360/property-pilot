package com.propertypilot.authservice.service;

import com.propertypilot.authservice.dto.LoginRequest;
import com.propertypilot.authservice.dto.LoginResponse;
import com.propertypilot.authservice.exception.InvalidCredentialsException;
import com.propertypilot.authservice.exception.UserNotFoundException;
import com.propertypilot.authservice.model.FirstAccessStep;
import com.propertypilot.authservice.model.User;
import com.propertypilot.authservice.repository.UserRepository;
import com.propertypilot.authservice.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        // 1. Trova utente
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login fallito: utente non trovato ({})", request.getEmail());
                    return new UserNotFoundException(request.getEmail());
                });


        // 2. Verifica password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login fallito: password errata per {}", request.getEmail());
            throw new InvalidCredentialsException();
        }

        FirstAccessStep next_step = user.getFirstAccessStep();
        // 3. Password reset obbligatorio
        if (Boolean.TRUE.equals(user.getPasswordResetRequired())) {
            log.info("Password reset richiesto per {}", user.getEmail());

            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordExpiresAt(LocalDateTime.now().plusHours(1));
            user = userRepository.save(user);
            return new LoginResponse(
                    null,                       // accessToken
                    "Bearer",                   // tokenType
                    user.getTenantKey(),        // tenantKey
                    user.getRoleEntity().getId(),
                    user.getEmail(),
                    user.getResetPasswordToken(),
                    true,                       // passwordResetRequired
                    false,                       // firstAccessRequired
                    next_step.getId()
            );
        }

        // 4. Primo accesso
        boolean firstAccessRequired = !Boolean.TRUE.equals(user.getFirstAccessCompleted());


        // 5. Genera JWT
        String token = jwtTokenProvider.generateToken(user);
        log.info("Login riuscito per {}", user.getEmail());

        // 6. Risposta finale
        return new LoginResponse(
                token,                         // accessToken
                "Bearer",                      // tokenType
                user.getTenantKey(),           // tenantKey
                user.getRoleEntity().getId(),
                user.getEmail(),
                user.getResetPasswordToken(),
                false,                         // passwordResetRequired
                firstAccessRequired,            // firstAccessRequired
                next_step.getId()
        );
    }


}