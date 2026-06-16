package com.unir.papertales.orders.service;

import com.unir.papertales.orders.controller.model.GetUsersResponseDto;
import com.unir.papertales.orders.controller.model.UserDto;
import com.unir.papertales.orders.exception.UserNotFoundException;
import com.unir.papertales.orders.repository.UserJpaRepository;
import com.unir.papertales.orders.repository.model.User;
import com.unir.papertales.orders.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUsersService {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public GetUsersResponseDto getUsers() {
        List<UserDto> users = userJpaRepository.findAll().stream()
                .map(userMapper::asUserDto)
                .toList();
        return GetUsersResponseDto.builder().users(users).build();
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userMapper.asUserDto(user);
    }
}
