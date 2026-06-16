package com.unir.papertales.orders.service;

import com.unir.papertales.orders.controller.model.UserDto;
import com.unir.papertales.orders.controller.model.WriteUserRequestDto;
import com.unir.papertales.orders.repository.UserJpaRepository;
import com.unir.papertales.orders.repository.model.User;
import com.unir.papertales.orders.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(WriteUserRequestDto request) {
        validateRequired(request);
        if (userJpaRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .avatar(request.getAvatar())
                .build();
        return userMapper.asUserDto(userJpaRepository.save(user));
    }

    private void validateRequired(WriteUserRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("User payload is required");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("User name is required");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("User email is required");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("User password is required");
        }
    }
}
