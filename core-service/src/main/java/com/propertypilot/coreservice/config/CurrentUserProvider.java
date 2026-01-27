package com.propertypilot.coreservice.config;

import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUserOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new RuntimeException("Autenticazione JWT non valida");
        }

        String email = jwtAuth.getToken().getSubject();
        String roleFromJwt = jwtAuth.getToken().getClaim("role").toString();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));

        // 🔥 Confronto ruolo JWT vs ruolo DB
        if (user != null &&
                user.getRoleEntity()  != null &&
                !user.getRoleEntity().getCode().isEmpty() &&
                !user.getRoleEntity().getCode().equals(roleFromJwt)) {
            throw new RuntimeException("Ruolo JWT non coerente con il ruolo attuale dell'utente");
        }

        return user;
    }
}
