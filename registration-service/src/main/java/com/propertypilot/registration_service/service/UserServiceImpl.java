package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.VerificationToken;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SenEmailService sendEmailService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public User registerUser(String email, String rawPassword, String role) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email già registrata");
        } else  if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email non valida");
        }


        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(false); // ora parte disabilitato
        userRepository.save(user);

        // 1. Genera token
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        // 2. Invia email
        String link = "http://localhost:8082/api/users/verify?token=" + token;
        String name = "Simone";
        sendEmailService.sendVerificationEmail(user.getEmail(),name, link);

        return user;
    }

    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

}