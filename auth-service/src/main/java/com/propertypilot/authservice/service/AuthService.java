package com.propertypilot.authservice.service;

import com.propertypilot.authservice.dto.LoginRequest;
import com.propertypilot.authservice.dto.LoginResponse;
import com.propertypilot.authservice.model.User;
import com.propertypilot.authservice.repository.UserRepository;
import com.propertypilot.authservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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
                .orElseThrow(() -> new RuntimeException("Credenziali non valide"));

        // 2. Verifica password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenziali non valide");
        }

        // 3. Password reset obbligatorio
        if (Boolean.TRUE.equals(user.getPasswordResetRequired())) {

            return new LoginResponse(
                    null,                       // accessToken
                    "Bearer",                   // tokenType
                    user.getTenantKey(),        // tenantKey
                    user.getRoleEntity().getCode(),             // role
                    true,                       // passwordResetRequired
                    false                       // firstAccessRequired
            );
        }

        // 4. Primo accesso
        boolean firstAccessRequired = !Boolean.TRUE.equals(user.getFirstAccessCompleted());

        // 5. Genera JWT
        String token = jwtTokenProvider.generateToken(user);

        // 6. Risposta finale
        return new LoginResponse(
                token,                         // accessToken
                "Bearer",                      // tokenType
                user.getTenantKey(),           // tenantKey
                user.getRoleEntity().getCode(),                // role
                false,                         // passwordResetRequired
                firstAccessRequired            // firstAccessRequired
        );
    }


}