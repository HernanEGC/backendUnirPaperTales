package com.unir.papertales.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.papertales.orders.client.CatalogueClient;
import com.unir.papertales.orders.controller.model.OrderDto;
import com.unir.papertales.orders.controller.model.WriteOrderItemDto;
import com.unir.papertales.orders.controller.model.WriteOrderRequestDto;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModifyOrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final OrderMapper orderMapper;
    private final CatalogueBookService catalogueBookService;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderDto modifyOrder(String orderId, WriteOrderRequestDto request) {
        Order existing = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        User user = request.getUserId() != null
                ? userJpaRepository.findById(request.getUserId())
                    .orElseThrow(() -> new OrderNotFoundException("User not found with id: " + request.getUserId()))
                : existing.getUser();

        Map<Long, CatalogueClient.BookSummary> summaries = catalogueBookService.validateItems(request.getItems());
        Map<Long, String> bookTitles = catalogueBookService.getBookTitles(summaries);

        BigDecimal total = request.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        existing.setUser(user);
        existing.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : existing.getOrderDate());
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
        existing.setTotal(total);

        orderItemJpaRepository.deleteByOrderId(orderId);
        orderJpaRepository.save(existing);

        List<OrderItem> items = orderMapper.asOrderItems(existing, request.getItems());
        List<OrderItem> savedItems = orderItemJpaRepository.saveAll(items);

        return orderMapper.asOrderDto(existing, savedItems, bookTitles);
    }

    @Transactional
    public OrderDto patchOrder(String orderId, String jsonPart) {
        Order existing = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        List<OrderItem> existingItems = orderItemJpaRepository.findByOrderId(orderId);
        try {
            JsonNode patch = objectMapper.readTree(jsonPart);
            validatePatchFieldNotNull(patch, "userId", "User id cannot be null");
            validatePatchFieldNotNull(patch, "items", "Order items cannot be null");
            validatePatchFieldNotNull(patch, "status", "Order status cannot be null");
            validatePatchFieldNotNull(patch, "orderDate", "Order date cannot be null");

            WriteOrderRequestDto base = WriteOrderRequestDto.builder()
                    .userId(existing.getUser().getId())
                    .orderDate(existing.getOrderDate())
                    .status(existing.getStatus())
                    .items(asWriteOrderItems(existingItems))
                    .build();

            JsonNode actualOrder = objectMapper.valueToTree(base);
            JsonMergePatch mergePatch = JsonMergePatch.fromJson(patch);
            JsonNode patchedNode = mergePatch.apply(actualOrder);
            WriteOrderRequestDto patched = objectMapper.treeToValue(patchedNode, WriteOrderRequestDto.class);

            boolean itemsProvided = patch.has("items");
            if (itemsProvided && (patched.getItems() == null || patched.getItems().isEmpty())) {
                throw new IllegalArgumentException("Order items are required");
            }

            if (patch.has("userId")) {
                User user = userJpaRepository.findById(patched.getUserId())
                        .orElseThrow(() -> new OrderNotFoundException("User not found with id: " + patched.getUserId()));
                existing.setUser(user);
            }
            if (patch.has("orderDate") && patched.getOrderDate() != null) {
                existing.setOrderDate(patched.getOrderDate());
            }
            if (patch.has("status") && patched.getStatus() != null) {
                existing.setStatus(patched.getStatus());
            }

            List<OrderItem> savedItems = existingItems;
            Map<Long, String> bookTitles;

            if (itemsProvided) {
                Map<Long, CatalogueClient.BookSummary> validatedSummaries = catalogueBookService.validateItems(patched.getItems());
                Map<Long, String> validatedTitles = catalogueBookService.getBookTitles(validatedSummaries);
                BigDecimal total = patched.getItems().stream()
                        .map(item -> {
                            if (item.getPrice() == null) {
                                throw new IllegalArgumentException("Price is required for book " + item.getBookId());
                            }
                            return BigDecimal.valueOf(item.getPrice())
                                    .multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1));
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                existing.setTotal(total);

                orderItemJpaRepository.deleteByOrderId(orderId);
                orderJpaRepository.save(existing);

                List<OrderItem> items = orderMapper.asOrderItems(existing, patched.getItems());
                savedItems = orderItemJpaRepository.saveAll(items);
                bookTitles = validatedTitles;
            } else {
                orderJpaRepository.save(existing);
                bookTitles = catalogueBookService.getBookTitles(existingItems);
            }

            return orderMapper.asOrderDto(existing, savedItems, bookTitles);
        } catch (JsonProcessingException | JsonPatchException e) {
            throw new IllegalArgumentException("Invalid JSON merge patch", e);
        }
    }

    private void validatePatchFieldNotNull(JsonNode patch, String field, String message) {
        if (patch != null && patch.has(field) && patch.get(field).isNull()) {
            throw new IllegalArgumentException(message);
        }
    }

    private List<WriteOrderItemDto> asWriteOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> WriteOrderItemDto.builder()
                        .bookId(item.getBookId())
                        .price(item.getPrice() != null ? item.getPrice().doubleValue() : null)
                        .quantity(item.getQuantity())
                        .build())
                .toList();
    }
}
