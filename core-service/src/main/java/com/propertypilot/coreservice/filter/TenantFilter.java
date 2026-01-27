package com.propertypilot.coreservice.filter;

import com.propertypilot.coreservice.context.TenantContext;
import com.propertypilot.coreservice.exceptionCustom.UserNotFoundException;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {

            String tenantFromJwt = jwtAuth.getToken().getClaimAsString("tenantKey");
            String email = jwtAuth.getToken().getSubject();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Utente non trovato: " + email));

            String tenantFromDb = user.getTenantKey();

            // 🔥 1. Se l’utente NON ha ancora un tenant → bypass
            if (tenantFromDb == null) {
                chain.doFilter(request, response);
                return;
            }

            // 🔥 2. Se il JWT non ha tenant → errore
            if (tenantFromJwt == null) {
                throw new UserNotFoundException("Tenant mancante nel token");
            }

            // 🔥 3. Se non coincidono → errore
            if (!tenantFromDb.equals(tenantFromJwt)) {
                throw new UserNotFoundException("L'utente non appartiene al tenant corrente");
            }

            // 🔥 4. Imposta tenant nel contesto
            TenantContext.setTenant(tenantFromJwt);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
