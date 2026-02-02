package com.dgliger.marketplace.product.kafka;

import com.dgliger.marketplace.common.event.KafkaTopics;
import com.dgliger.marketplace.common.event.StockUpdateEvent;
import com.dgliger.marketplace.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockUpdateConsumer {

    private final ProductService productService;

    @KafkaListener(topics = KafkaTopics.STOCK_UPDATE, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeStockUpdate(StockUpdateEvent event) {
        log.info("Received StockUpdateEvent for productId: {}, quantity: {}, operation: {}",
                event.getProductId(), event.getQuantity(), event.getOperation());

        try {
            if ("DECREASE".equals(event.getOperation())) {
                productService.updateStock(event.getProductId(), event.getQuantity());
                log.info("Stock updated successfully for productId: {}", event.getProductId());
            } else if ("INCREASE".equals(event.getOperation())) {
                productService.increaseStock(event.getProductId(), event.getQuantity());
                log.info("Stock increased successfully for productId: {}", event.getProductId());
            }
        } catch (Exception e) {
            log.error("Failed to update stock for productId: {}, error: {}", event.getProductId(), e.getMessage());
        }
    }
}
