package com.propertypilot.apigatway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtPresenceFilter implements GlobalFilter, Ordered {

    // Lista di percorsi che richiedono almeno il token
    private static final List<String> PROTECTED_PATHS = Arrays.asList(
            "/api/core/**"   // tutti gli endpoint core richiedono Authorization header
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        // Controlla se il percorso richiede token
        boolean requiresToken = PROTECTED_PATHS.stream().anyMatch(path::startsWith);

        if (requiresToken) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // Inoltra sempre l'header Authorization ai microservizi downstream
        return chain.filter(exchange.mutate()
                .request(r -> {
                    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (authHeader != null) {
                        r.header(HttpHeaders.AUTHORIZATION, authHeader);
                    }
                })
                .build()
        );
    }

    @Override
    public int getOrder() {
        return -1;  // filtro eseguito prima di altri filtri
    }
}