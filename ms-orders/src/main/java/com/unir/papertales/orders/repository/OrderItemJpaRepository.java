package com.unir.papertales.orders.repository;

import com.unir.papertales.orders.repository.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}
