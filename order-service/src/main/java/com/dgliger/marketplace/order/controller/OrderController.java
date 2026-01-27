package com.dgliger.marketplace.order.controller;



import com.dgliger.marketplace.common.dto.ApiResponse;
import com.dgliger.marketplace.order.dto.OrderRequest;
import com.dgliger.marketplace.order.dto.OrderResponse;
import com.dgliger.marketplace.order.enums.OrderStatus;
import com.dgliger.marketplace.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        OrderResponse order = orderService.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Roles") String rolesHeader) {

        List<String> roles = parseRoles(rolesHeader);

        if (roles.contains("ADMIN")) {
            List<OrderResponse> orders = orderService.getAllOrders(roles);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } else {
            List<OrderResponse> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(ApiResponse.success(orders));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Roles") String rolesHeader) {

        List<String> roles = parseRoles(rolesHeader);
        OrderResponse order = orderService.getOrderById(id, userId, roles);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Roles") String rolesHeader) {

        List<String> roles = parseRoles(rolesHeader);
        OrderResponse order = orderService.updateOrderStatus(id, status, userId, roles);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }

    private List<String> parseRoles(String rolesHeader) {
        String cleaned = rolesHeader.replaceAll("[\\\\[\\\\]]", "");
        return Arrays.asList(cleaned.split(",\\\\s*"));
    }
}