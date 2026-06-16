package com.unir.papertales.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.ttl-seconds:300}")
    private long ttlSeconds;

    public String generatePhantomTokenPattern(String username, String role, Long userId) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(ttlSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("relatos-papel-auth")
                .issuedAt(now)
                .expiresAt(expiration)
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", List.of(role))
                .build();

        String internalJwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        String opaqueToken = "rp_opaque_" + UUID.randomUUID().toString().replace("-", "");

        redisTemplate.opsForValue().set(opaqueToken, internalJwt, ttlSeconds, TimeUnit.SECONDS);
        return opaqueToken;
    }

    public void revokeOpaqueToken(String opaqueToken) {
        redisTemplate.delete(opaqueToken);
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }
}

