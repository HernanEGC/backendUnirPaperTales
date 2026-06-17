package com.unir.papertales.users.dto;

public record RegisterUserResponse(
        Long id,
        String email,
        String role,
        boolean enabled
) {
}

