package com.unir.papertales.orders.utils;

import com.unir.papertales.orders.controller.model.UserDto;
import com.unir.papertales.orders.repository.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto asUserDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }
}
