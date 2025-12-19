package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String email, String rawPassword, String role) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email già registrata");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true); // sarà attivato via email verification

        return userRepository.save(user);
    }

    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        user.setEnabled(true);
        userRepository.save(user);
    }
}