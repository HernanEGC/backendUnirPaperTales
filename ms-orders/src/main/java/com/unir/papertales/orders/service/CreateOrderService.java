package com.unir.papertales.orders.service;

import com.unir.papertales.orders.controller.model.OrderDto;
import com.unir.papertales.orders.controller.model.WriteOrderRequestDto;
import com.unir.papertales.orders.client.CatalogueClient;
import com.unir.papertales.orders.exception.OrderNotFoundException;
import com.unir.papertales.orders.repository.OrderItemJpaRepository;
import com.unir.papertales.orders.repository.OrderJpaRepository;
import com.unir.papertales.orders.repository.UserJpaRepository;
import com.unir.papertales.orders.repository.model.Order;
import com.unir.papertales.orders.repository.model.OrderItem;
import com.unir.papertales.orders.repository.model.User;
import com.unir.papertales.orders.utils.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final OrderMapper orderMapper;
    private final CatalogueBookService catalogueBookService;

    @Transactional
    public OrderDto createOrder(WriteOrderRequestDto request) {
        User user = userJpaRepository.findById(request.getUserId())
                .orElseThrow(() -> new OrderNotFoundException("User not found with id: " + request.getUserId()));

        String orderId = "PED-" + (1000 + (int)(Math.random() * 9000));
        while (orderJpaRepository.existsById(orderId)) {
            orderId = "PED-" + (1000 + (int)(Math.random() * 9000));
        }

        Map<Long, CatalogueClient.BookSummary> summaries = catalogueBookService.validateItems(request.getItems());
        catalogueBookService.decreaseStock(request.getItems(), summaries);
        Map<Long, String> bookTitles = catalogueBookService.getBookTitles(summaries);

        BigDecimal total = request.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .orderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now())
                .status(request.getStatus() != null ? request.getStatus() : "Pendiente")
                .total(total)
                .build();

        order = orderJpaRepository.save(order);

        List<OrderItem> items = orderMapper.asOrderItems(order, request.getItems());
        List<OrderItem> savedItems = orderItemJpaRepository.saveAll(items);

        return orderMapper.asOrderDto(order, savedItems, bookTitles);
    }
}
