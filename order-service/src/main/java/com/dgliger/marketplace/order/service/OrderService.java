package com.dgliger.marketplace.order.service;



import com.dgliger.marketplace.common.exception.BusinessException;
import com.dgliger.marketplace.common.exception.ResourceNotFoundException;
import com.dgliger.marketplace.order.client.ProductClient;
import com.dgliger.marketplace.order.dto.OrderItemDto;
import com.dgliger.marketplace.order.dto.OrderRequest;
import com.dgliger.marketplace.order.dto.OrderResponse;
import com.dgliger.marketplace.order.entity.Order;
import com.dgliger.marketplace.order.entity.OrderItem;
import com.dgliger.marketplace.order.enums.OrderStatus;
import com.dgliger.marketplace.order.mapper.OrderMapper;
import com.dgliger.marketplace.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request, Long userId) {
        // Validate stock for all items
        for (OrderItemDto itemDto : request.getItems()) {
            Boolean stockAvailable = productClient.checkStock(itemDto.getProductId(), itemDto.getQuantity());
            if (!stockAvailable) {
                throw new BusinessException("Insufficient stock for product ID: " + itemDto.getProductId());
            }
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(request.getShippingAddress());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDto itemDto : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemDto.getProductId());
            orderItem.setQuantity(itemDto.getQuantity());

            // In a real scenario, fetch actual product details from product service
            orderItem.setProductName("Product " + itemDto.getProductId());
            orderItem.setPrice(BigDecimal.valueOf(100)); // Mock price

            BigDecimal subtotal = orderItem.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            orderItem.setSubtotal(subtotal);
            totalAmount = totalAmount.add(subtotal);

            order.addItem(orderItem);

            // Update stock
            productClient.updateStock(itemDto.getProductId(), itemDto.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toResponseList(orders);
    }

    public OrderResponse getOrderById(Long id, Long userId, List<String> roles) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (!roles.contains("ADMIN") && !order.getUserId().equals(userId)) {
            throw new BusinessException("You can only view your own orders");
        }

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status, Long userId, List<String> roles) {
        if (!roles.contains("ADMIN")) {
            throw new BusinessException("Only admins can update order status");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponse(updatedOrder);
    }

    public List<OrderResponse> getAllOrders(List<String> roles) {
        if (!roles.contains("ADMIN")) {
            throw new BusinessException("Only admins can view all orders");
        }

        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseList(orders);
    }
}