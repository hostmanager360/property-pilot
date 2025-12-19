package com.propertypilot.authservice.service;

import com.propertypilot.authservice.dto.LoginRequest;
import com.propertypilot.authservice.dto.LoginResponse;
import com.propertypilot.authservice.model.User;
import com.propertypilot.authservice.repository.UserRepository;
import com.propertypilot.authservice.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenziali non valide"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Utente non attivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenziali non valide");
        }

        String token = jwtTokenProvider.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                user.getTenantKey(),
                user.getRole()
        );
    }

}