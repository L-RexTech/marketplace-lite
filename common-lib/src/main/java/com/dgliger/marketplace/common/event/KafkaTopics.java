package com.dgliger.marketplace.common.event;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order-created";
    public static final String ORDER_STATUS_CHANGED = "order-status-changed";
    public static final String STOCK_UPDATE = "stock-update";
    
    private KafkaTopics() {}
}
