package com.dgliger.marketplace.order.controller;


import com.dgliger.marketplace.common.dto.ApiResponse;
import com.dgliger.marketplace.order.dto.OrderRequest;
import com.dgliger.marketplace.order.dto.OrderResponse;
import com.dgliger.marketplace.order.enums.OrderStatus;
import com.dgliger.marketplace.order.security.UserPrincipal;
import com.dgliger.marketplace.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        OrderResponse order = orderService.createOrder(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<String> roles = principal.getRoles();

        if (roles.contains("ADMIN")) {
            // ADMIN sees all orders
            List<OrderResponse> orders = orderService.getAllOrders(roles);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } else {
            // SELLER and BUYER see their own orders
            List<OrderResponse> orders = orderService.getUserOrders(principal.getUserId());
            return ResponseEntity.ok(ApiResponse.success(orders));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        OrderResponse order = orderService.getOrderById(id, principal.getUserId(), principal.getRoles());
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {

        OrderResponse order = orderService.updateOrderStatus(id, status, principal.getUserId(), principal.getRoles());
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
    }
}
