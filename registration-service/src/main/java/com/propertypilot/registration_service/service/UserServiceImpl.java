package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.exception.EmailAlreadyExistsException;
import com.propertypilot.registration_service.exception.InvalidEmailException;
import com.propertypilot.registration_service.exception.PasswordMismatchException;
import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.UserDto;
import com.propertypilot.registration_service.model.VerificationToken;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public User registerUser(UserDto dto) {

        // 1. Validazione password
        validatePassword(dto);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }



        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
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
        String name = user.getEmail();
        sendEmailService.sendVerificationEmail(user.getEmail(),name, link);

        return user;
    }

    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

    private void validatePassword(UserDto dto) {
        if (!dto.getPassword().equals(dto.getConfermaPassword())) {
            throw new PasswordMismatchException("Le password non coincidono");
        }
    }

}