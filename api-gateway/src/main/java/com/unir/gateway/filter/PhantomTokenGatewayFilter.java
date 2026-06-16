package com.unir.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PhantomTokenGatewayFilter implements GlobalFilter, Ordered {

    private static final String AUTH_PREFIX = "Bearer ";

    private final ReactiveStringRedisTemplate redisTemplate;

    public PhantomTokenGatewayFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isPublicPath(path) || !isProtectedPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(AUTH_PREFIX)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        String opaqueToken = authHeader.substring(AUTH_PREFIX.length());
        return redisTemplate.opsForValue().get(opaqueToken)
                .flatMap(internalJwt -> {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .headers(headers -> headers.setBearerAuth(internalJwt))
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(onError(exchange, HttpStatus.UNAUTHORIZED));
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/auth/") || path.startsWith("/actuator/");
    }

    private boolean isProtectedPath(String path) {
        return path.startsWith("/catalogo/") || path.startsWith("/ordenes/");
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

