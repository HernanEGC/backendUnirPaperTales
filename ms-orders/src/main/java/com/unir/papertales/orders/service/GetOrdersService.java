package com.unir.papertales.orders.service;

import com.unir.papertales.orders.controller.model.GetOrdersResponseDto;
import com.unir.papertales.orders.controller.model.OrderDto;
import com.unir.papertales.orders.exception.OrderNotFoundException;
import com.unir.papertales.orders.repository.OrderItemJpaRepository;
import com.unir.papertales.orders.repository.OrderJpaRepository;
import com.unir.papertales.orders.repository.model.Order;
import com.unir.papertales.orders.repository.model.OrderItem;
import com.unir.papertales.orders.utils.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GetOrdersService {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderMapper orderMapper;
    private final CatalogueBookService catalogueBookService;

    @Transactional(readOnly = true)
    public GetOrdersResponseDto getOrders() {
        List<Order> orders = orderJpaRepository.findAll();
        List<OrderDto> dtos = orders.stream()
                .map(orderMapper::asOrderDtoSimple)
                .toList();
        return GetOrdersResponseDto.builder().orders(dtos).build();
    }

    @Transactional(readOnly = true)
    public GetOrdersResponseDto getOrdersByUser(Long userId) {
        List<Order> orders = orderJpaRepository.findByUserId(userId);
        List<OrderDto> dtos = orders.stream()
                .map(orderMapper::asOrderDtoSimple)
                .toList();
        return GetOrdersResponseDto.builder().orders(dtos).build();
    }

    @Transactional(readOnly = true)
    public GetOrdersResponseDto getOrdersWithItems() {
        List<Order> orders = orderJpaRepository.findAllWithItems();
        List<OrderItem> allItems = orders.stream()
                .flatMap(order -> {
                    List<OrderItem> items = order.getItems();
                    return items == null ? Stream.empty() : items.stream();
                })
                .toList();
        Map<Long, String> bookTitles = catalogueBookService.getBookTitles(allItems);
        List<OrderDto> dtos = orders.stream()
                .map(order -> {
                    List<OrderItem> items = order.getItems();
                    return orderMapper.asOrderDto(order,
                            items != null ? items : Collections.emptyList(),
                            bookTitles);
                })
                .toList();
        return GetOrdersResponseDto.builder().orders(dtos).build();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(String orderId) {
        Order order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        List<OrderItem> items = orderItemJpaRepository.findByOrderId(orderId);
        Map<Long, String> bookTitles = catalogueBookService.getBookTitles(items);
        return orderMapper.asOrderDto(order, items, bookTitles);
    }
}
