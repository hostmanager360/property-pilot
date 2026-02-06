package com.propertypilot.registration_service.util;

import com.propertypilot.registration_service.model.User;
import com.propertypilot.registration_service.repository.UserRepository;
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
        String role = jwtAuth.getToken().getClaim("role");

        // tenantKey può essere null → NON usare toString()
        String tenantKey = jwtAuth.getToken().getClaim("tenantKey");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));

        // Verifica ruolo coerente
        if (!user.getRoleEntity().getCode().equals(role)) {
            throw new RuntimeException("Ruolo JWT non coerente con DB");
        }

        // 🔥 Se è OWNER → NON deve avere tenantKey
        if (role.equals("OWNER")) {
            return user; // tutto ok, nessun tenantKey richiesto
        }

        // 🔥 Se NON è OWNER → tenantKey DEVE esistere
        if (tenantKey == null || tenantKey.isBlank()) {
            throw new RuntimeException("TenantKey mancante per ruolo non-OWNER");
        }

        // opzionale: puoi verificare che il tenantKey del JWT corrisponda a quello del DB
        if (!tenantKey.equals(user.getTenantKey())) {
            throw new RuntimeException("TenantKey JWT non coerente con DB");
        }

        return user;


    }
}
