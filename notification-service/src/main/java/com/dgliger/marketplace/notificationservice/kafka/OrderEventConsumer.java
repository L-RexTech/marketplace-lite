package com.dgliger.marketplace.notificationservice.kafka;

import com.dgliger.marketplace.common.event.KafkaTopics;
import com.dgliger.marketplace.common.event.OrderCreatedEvent;
import com.dgliger.marketplace.common.event.OrderStatusChangedEvent;
import com.dgliger.marketplace.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"spring.json.value.default.type=com.dgliger.marketplace.common.event.OrderCreatedEvent"}
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId: {}", event.getOrderId());
        emailService.sendOrderCreatedEmail(event);
    }

    @KafkaListener(
            topics = KafkaTopics.ORDER_STATUS_CHANGED,
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"spring.json.value.default.type=com.dgliger.marketplace.common.event.OrderStatusChangedEvent"}
    )
    public void consumeOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Received OrderStatusChangedEvent for orderId: {}, status: {} -> {}",
                event.getOrderId(), event.getPreviousStatus(), event.getNewStatus());
        emailService.sendOrderStatusChangedEmail(event);
    }
}
