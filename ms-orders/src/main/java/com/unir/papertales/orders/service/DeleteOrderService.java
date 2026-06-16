package com.unir.papertales.orders.service;

import com.unir.papertales.orders.exception.OrderNotFoundException;
import com.unir.papertales.orders.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteOrderService {

    private final OrderJpaRepository orderJpaRepository;

    @Transactional
    public void deleteOrder(String orderId) {
        if (!orderJpaRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with id: " + orderId);
        }
        orderJpaRepository.deleteById(orderId);
    }
}

