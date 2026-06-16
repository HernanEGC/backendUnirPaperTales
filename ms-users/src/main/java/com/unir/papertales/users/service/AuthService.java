package com.unir.papertales.users.service;

import com.unir.papertales.users.dto.AuthResponse;
import com.unir.papertales.users.dto.LoginRequest;
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

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header is required");
        }

        tokenService.revokeOpaqueToken(authorizationHeader.substring(7));
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }
}

