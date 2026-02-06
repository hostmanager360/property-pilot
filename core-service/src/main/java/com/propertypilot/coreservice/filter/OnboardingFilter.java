package com.propertypilot.coreservice.filter;

import com.propertypilot.coreservice.config.CurrentUserProvider;
import com.propertypilot.coreservice.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OnboardingFilter extends OncePerRequestFilter {

    private final CurrentUserProvider currentUserProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Solo endpoint first-access
        if (!path.startsWith("/api/core/private/first-access")) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = currentUserProvider.getCurrentUserOrThrow();

        long step = user.getFirstAccessStep().getId();
        boolean completed = Boolean.TRUE.equals(user.getFirstAccessCompleted());

        // Se ha già completato → NON deve più accedere a first-access
        if (completed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Primo accesso già completato");
            return;
        }

        switch ((int) step) {
            case 1:
                if (!path.endsWith("/create-tenant")) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Completa prima la creazione del tenant");
                    return;
                }
                break;

            case 2:
                if (!path.endsWith("/user-detail")) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Completa prima i tuoi dati personali");
                    return;
                }
                break;

            case 3:
                if (!path.endsWith("/complete")) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Primo accesso già completato");
                    return;
                }
                break;

            default:
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Stato onboarding non valido");
                return;
        }

        filterChain.doFilter(request, response);
    }
}