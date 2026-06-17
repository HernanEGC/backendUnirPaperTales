package com.unir.papertales.users.service;

import com.unir.papertales.users.dto.AuthResponse;
import com.unir.papertales.users.dto.LoginRequest;
import com.unir.papertales.users.dto.RegisterUserRequest;
import com.unir.papertales.users.dto.RegisterUserResponse;
import com.unir.papertales.users.model.User;
import com.unir.papertales.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROLE_LECTOR = "ROLE_LECTOR";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(this::invalidCredentials);

        if (!user.isEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw invalidCredentials();
        }

        String opaqueToken = tokenService.generatePhantomTokenPattern(user.getEmail(), user.getRole(), user.getId());
        return new AuthResponse(opaqueToken, "Bearer", tokenService.getTtlSeconds());
    }

    public RegisterUserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        String role = resolveRole(request.getRole());
        boolean enabled = request.getEnabled() == null || request.getEnabled();

        User savedUser = userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(enabled)
                .build());

        return new RegisterUserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getRole(), savedUser.isEnabled());
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header is required");
        }

        tokenService.revokeOpaqueToken(authorizationHeader.substring(7));
    }

    private String resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return ROLE_LECTOR;
        }

        String normalizedRole = role.trim().toUpperCase();
        if (!ROLE_LECTOR.equals(normalizedRole) && !ROLE_ADMIN.equals(normalizedRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role no válido. Usa ROLE_LECTOR o ROLE_ADMIN");
        }
        return normalizedRole;
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }
}
