package com.unir.papertales.orders.utils;

import com.unir.papertales.orders.controller.model.OrderDto;
import com.unir.papertales.orders.controller.model.OrderItemDto;
import com.unir.papertales.orders.controller.model.WriteOrderItemDto;
import com.unir.papertales.orders.repository.model.Order;
import com.unir.papertales.orders.repository.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class OrderMapper {

    public OrderDto asOrderDto(Order order, List<OrderItem> items, Map<Long, String> bookTitles) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .total(order.getTotal().doubleValue())
                .items(items.stream()
                        .map(item -> asOrderItemDto(item, bookTitles.get(item.getBookId())))
                        .toList())
                .build();
    }

    public OrderDto asOrderDtoSimple(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .total(order.getTotal().doubleValue())
                .items(Collections.emptyList())
                .build();
    }

    public OrderItemDto asOrderItemDto(OrderItem item, String bookTitle) {
        return OrderItemDto.builder()
                .id(item.getId())
                .bookId(item.getBookId())
                .bookTitle(bookTitle)
                .price(item.getPrice().doubleValue())
                .quantity(item.getQuantity())
                .build();
    }

    public List<OrderItem> asOrderItems(Order order, List<WriteOrderItemDto> dtos) {
        return dtos.stream()
                .map(dto -> OrderItem.builder()
                        .order(order)
                        .bookId(dto.getBookId())
                        .price(BigDecimal.valueOf(dto.getPrice()))
                        .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                        .build())
                .toList();
    }
}
