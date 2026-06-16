package com.unir.papertales.orders.controller;

import com.unir.papertales.orders.controller.model.GetOrdersResponseDto;
import com.unir.papertales.orders.controller.model.OrderDto;
import com.unir.papertales.orders.controller.model.WriteOrderRequestDto;
import com.unir.papertales.orders.service.CreateOrderService;
import com.unir.papertales.orders.service.DeleteOrderService;
import com.unir.papertales.orders.service.GetOrdersService;
import com.unir.papertales.orders.service.ModifyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrdersController {

    private final GetOrdersService getOrdersService;
    private final CreateOrderService createOrderService;
    private final ModifyOrderService modifyOrderService;
    private final DeleteOrderService deleteOrderService;

    /** Lista todas las órdenes */
    @GetMapping("/orders")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<GetOrdersResponseDto> getOrders(
            @RequestParam(name = "includeItems", required = false, defaultValue = "false") boolean includeItems) {
        return ResponseEntity.ok(includeItems
                ? getOrdersService.getOrdersWithItems()
                : getOrdersService.getOrders());
    }

    /** Lista órdenes de un usuario específico */
    @GetMapping("/orders/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<GetOrdersResponseDto> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(getOrdersService.getOrdersByUser(userId));
    }

    /** Detalle de una orden, incluye items con book_title desde catálogo vía HTTP */
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(getOrdersService.getOrder(orderId));
    }

    /** Crea una nueva orden */
    @PostMapping("/orders")
    @PreAuthorize("hasAnyAuthority('ROLE_LECTOR', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDto> createOrder(@RequestBody WriteOrderRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createOrderService.createOrder(request));
    }

    /** Reemplaza completamente una orden */
    @PutMapping("/orders/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable String orderId,
            @RequestBody WriteOrderRequestDto request) {
        return ResponseEntity.ok(modifyOrderService.modifyOrder(orderId, request));
    }

    /** Actualización parcial de una orden (JSON Merge Patch) */
    @PatchMapping("/orders/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderDto> patchOrder(
            @PathVariable String orderId,
            @RequestBody String jsonPart) {
        return ResponseEntity.ok(modifyOrderService.patchOrder(orderId, jsonPart));
    }

    /** Elimina una orden */
    @DeleteMapping("/orders/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        deleteOrderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
