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

    private static final List<String> PROTECTED_PREFIXES = List.of(
            "/api/core/private"
    );

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/core/public/" , "/api/auth"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        boolean isProtected = PROTECTED_PREFIXES.stream().anyMatch(p -> path.startsWith(p));
        boolean isPublic = PUBLIC_PREFIXES.stream().anyMatch(p -> path.startsWith(p));

        if (isProtected && !isPublic) {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;  // filtro eseguito prima di altri filtri
    }
}