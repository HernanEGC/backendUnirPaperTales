package com.unir.papertales.users.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}

