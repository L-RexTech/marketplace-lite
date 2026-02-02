package com.dgliger.marketplace.order.service;


import com.dgliger.marketplace.common.event.OrderCreatedEvent;
import com.dgliger.marketplace.common.event.OrderStatusChangedEvent;
import com.dgliger.marketplace.common.exception.BusinessException;
import com.dgliger.marketplace.common.exception.ResourceNotFoundException;
import com.dgliger.marketplace.order.client.ProductClient;
import com.dgliger.marketplace.order.dto.OrderItemDto;
import com.dgliger.marketplace.order.dto.OrderRequest;
import com.dgliger.marketplace.order.dto.OrderResponse;
import com.dgliger.marketplace.order.entity.Order;
import com.dgliger.marketplace.order.entity.OrderItem;
import com.dgliger.marketplace.order.enums.OrderStatus;
import com.dgliger.marketplace.order.kafka.OrderEventProducer;
import com.dgliger.marketplace.order.mapper.OrderMapper;
import com.dgliger.marketplace.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderEventProducer orderEventProducer;

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
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Publish Kafka events for stock updates
        for (OrderItem item : savedOrder.getItems()) {
            orderEventProducer.sendStockUpdateEvent(item.getProductId(), item.getQuantity(), savedOrder.getId());
        }

        // Publish order created event
        publishOrderCreatedEvent(savedOrder);

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

        String previousStatus = order.getStatus().name();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        // Publish order status changed event
        OrderStatusChangedEvent event = OrderStatusChangedEvent.builder()
                .orderId(updatedOrder.getId())
                .userId(updatedOrder.getUserId())
                .previousStatus(previousStatus)
                .newStatus(status.name())
                .updatedAt(LocalDateTime.now())
                .build();
        orderEventProducer.sendOrderStatusChangedEvent(event);

        return orderMapper.toResponse(updatedOrder);
    }

    public List<OrderResponse> getAllOrders(List<String> roles) {
        if (!roles.contains("ADMIN")) {
            throw new BusinessException("Only admins can view all orders");
        }

        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseList(orders);
    }

    private void publishOrderCreatedEvent(Order order) {
        List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .items(itemEvents)
                .createdAt(LocalDateTime.now())
                .build();

        orderEventProducer.sendOrderCreatedEvent(event);
    }
}
