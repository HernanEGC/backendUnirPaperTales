package com.unir.papertales.users.config;

import com.unir.papertales.users.model.User;
import com.unir.papertales.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedUsers() {
        return args -> {
            if (!userRepository.existsByEmail("lector@unir.com")) {
                userRepository.save(User.builder()
                        .email("lector@unir.com")
                        .password(passwordEncoder.encode("password123"))
                        .role("ROLE_LECTOR")
                        .enabled(true)
                        .build());
            }

            if (!userRepository.existsByEmail("admin@unir.com")) {
                userRepository.save(User.builder()
                        .email("admin@unir.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .enabled(true)
                        .build());
            }
        };
    }
}

