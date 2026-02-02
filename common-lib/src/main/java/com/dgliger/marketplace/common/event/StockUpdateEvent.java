package com.dgliger.marketplace.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateEvent {
    private Long productId;
    private Integer quantity;
    private String operation; // DECREASE, INCREASE
    private Long orderId;
    private LocalDateTime timestamp;
}
