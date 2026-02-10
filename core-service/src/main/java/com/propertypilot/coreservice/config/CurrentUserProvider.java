package com.propertypilot.coreservice.config;

import com.propertypilot.coreservice.exceptionCustom.ForbiddenException;
import com.propertypilot.coreservice.exceptionCustom.UserNotFoundException;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUserOrThrow() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            log.warn("Accesso negato: autenticazione non JWT");
            throw new BadCredentialsException("Token JWT non valido");
        }

        String email = jwtAuth.getToken().getSubject();
        String roleFromJwt = jwtAuth.getToken().getClaimAsString("role");
        String tenantFromJwt = jwtAuth.getToken().getClaimAsString("tenantKey");
        Long stepFromJwt = jwtAuth.getToken().getClaim("firstAccessStep");

        log.debug("Richiesta autenticata: email={}, role={}, tenantKey={}, step={}",
                email, roleFromJwt, tenantFromJwt, stepFromJwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Utente non trovato per email {}", email);
                    return new UserNotFoundException("Utente non trovato");
                });

        // Ruolo coerente
        if (!user.getRoleEntity().getCode().equals(roleFromJwt)) {
            log.warn("Ruolo incoerente: JWT={}, DB={}", roleFromJwt, user.getRoleEntity().getCode());
            throw new ForbiddenException("Ruolo JWT non coerente con DB");
        }

        // Tenant coerente (solo se l'utente ha un tenant)
        if (user.getTenantKey() != null) {
            if (tenantFromJwt == null || !user.getTenantKey().equals(tenantFromJwt)) {
                log.warn("Tenant incoerente: JWT={}, DB={}", tenantFromJwt, user.getTenantKey());
                throw new ForbiddenException("Tenant non coerente");
            }
        }

        // Step onboarding coerente
        if (user.getFirstAccessStep() != null) {
            if (stepFromJwt == null || !user.getFirstAccessStep().getId().equals(stepFromJwt)) {
                log.warn("Step onboarding incoerente: JWT={}, DB={}",
                        stepFromJwt, user.getFirstAccessStep().getId());
                throw new ForbiddenException("Step onboarding non coerente");
            }
        }

        return user;
    }
}