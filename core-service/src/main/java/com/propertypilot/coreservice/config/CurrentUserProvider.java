package com.propertypilot.coreservice.config;

import com.propertypilot.coreservice.exceptionCustom.ForbiddenException;
import com.propertypilot.coreservice.exceptionCustom.UserNotFoundException;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
            throw new AuthenticationException("Token JWT non valido") {};
        }

        String email = jwtAuth.getToken().getSubject();
        String roleFromJwt = jwtAuth.getToken().getClaimAsString("role");
        String tenantFromJwt = jwtAuth.getToken().getClaimAsString("tenantKey");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utente non trovato"));

        // Ruolo coerente
        if (!user.getRoleEntity().getCode().equals(roleFromJwt)) {
            throw new ForbiddenException("Ruolo JWT non coerente con DB");
        }

        // Tenant coerente
        if (user.getTenantKey() != null && tenantFromJwt != null &&
                !user.getTenantKey().equals(tenantFromJwt)) {
            throw new ForbiddenException("Tenant non coerente");
        }
        long stepFromJwt = jwtAuth.getToken().getClaim("firstAccessStep");

        if (!user.getFirstAccessStep().getId().equals(stepFromJwt)) {
            throw new ForbiddenException("Step onboarding non coerente");
        }
        return user;
    }

}
