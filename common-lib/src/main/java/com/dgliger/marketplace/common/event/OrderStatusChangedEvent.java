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
public class OrderStatusChangedEvent {
    private Long orderId;
    private Long userId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime updatedAt;
}
