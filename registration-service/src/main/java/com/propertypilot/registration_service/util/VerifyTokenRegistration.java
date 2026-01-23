package com.propertypilot.registration_service.util;

import com.propertypilot.registration_service.exception.TokenExpiredException;
import com.propertypilot.registration_service.exception.TokenNotFoundException;
import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.model.VerificationToken;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VerifyTokenRegistration {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    UserRepository userRepository;

    @Transactional
    public void verifyToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token non valido"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token scaduto");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // opzionale: elimina token dopo l’uso
        verificationTokenRepository.delete(verificationToken);
    }
}
