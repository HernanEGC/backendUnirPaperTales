package com.unir.papertales.orders.service;

import com.unir.papertales.orders.exception.UserNotFoundException;
import com.unir.papertales.orders.repository.UserJpaRepository;
import com.unir.papertales.orders.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

    private final UserJpaRepository userJpaRepository;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        userJpaRepository.delete(user);
    }
}
