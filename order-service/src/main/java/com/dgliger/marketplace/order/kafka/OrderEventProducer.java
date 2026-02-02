package com.dgliger.marketplace.order.kafka;

import com.dgliger.marketplace.common.event.KafkaTopics;
import com.dgliger.marketplace.common.event.OrderCreatedEvent;
import com.dgliger.marketplace.common.event.OrderStatusChangedEvent;
import com.dgliger.marketplace.common.event.StockUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Sending OrderCreatedEvent for orderId: {}", event.getOrderId());
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, String.valueOf(event.getOrderId()), event);
    }

    public void sendOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        log.info("Sending OrderStatusChangedEvent for orderId: {}", event.getOrderId());
        kafkaTemplate.send(KafkaTopics.ORDER_STATUS_CHANGED, String.valueOf(event.getOrderId()), event);
    }

    public void sendStockUpdateEvent(Long productId, Integer quantity, Long orderId) {
        StockUpdateEvent event = StockUpdateEvent.builder()
                .productId(productId)
                .quantity(quantity)
                .operation("DECREASE")
                .orderId(orderId)
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Sending StockUpdateEvent for productId: {}", productId);
        kafkaTemplate.send(KafkaTopics.STOCK_UPDATE, String.valueOf(productId), event);
    }
}
