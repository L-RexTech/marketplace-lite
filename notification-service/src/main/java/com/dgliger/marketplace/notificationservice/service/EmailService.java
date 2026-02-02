package com.dgliger.marketplace.notificationservice.service;

import com.dgliger.marketplace.common.event.OrderCreatedEvent;
import com.dgliger.marketplace.common.event.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.recipient.email}")
    private String recipientEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderCreatedEmail(OrderCreatedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("🛒 New Order Created - Order #" + event.getOrderId());
            message.setText(buildOrderCreatedEmailBody(event));

            mailSender.send(message);
            log.info("Order created email sent successfully for order #{}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send order created email for order #{}: {}", event.getOrderId(), e.getMessage());
        }
    }

    public void sendOrderStatusChangedEmail(OrderStatusChangedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("📦 Order Status Updated - Order #" + event.getOrderId());
            message.setText(buildOrderStatusChangedEmailBody(event));

            mailSender.send(message);
            log.info("Order status changed email sent successfully for order #{}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send order status email for order #{}: {}", event.getOrderId(), e.getMessage());
        }
    }

    private String buildOrderCreatedEmailBody(OrderCreatedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("         NEW ORDER NOTIFICATION\n");
        sb.append("===========================================\n\n");
        sb.append("Order ID: #").append(event.getOrderId()).append("\n");
        sb.append("User ID: ").append(event.getUserId()).append("\n");
        sb.append("Created At: ").append(event.getCreatedAt()).append("\n");
        sb.append("Shipping Address: ").append(event.getShippingAddress()).append("\n\n");
        
        sb.append("-------------------------------------------\n");
        sb.append("ORDER ITEMS:\n");
        sb.append("-------------------------------------------\n");
        
        if (event.getItems() != null) {
            for (OrderCreatedEvent.OrderItemEvent item : event.getItems()) {
                sb.append("• ").append(item.getProductName())
                  .append(" (ID: ").append(item.getProductId()).append(")\n");
                sb.append("  Quantity: ").append(item.getQuantity())
                  .append(" x $").append(item.getPrice()).append("\n");
            }
        }
        
        sb.append("\n-------------------------------------------\n");
        sb.append("TOTAL AMOUNT: $").append(event.getTotalAmount()).append("\n");
        sb.append("-------------------------------------------\n\n");
        sb.append("Thank you for using Marketplace Lite!\n");
        
        return sb.toString();
    }

    private String buildOrderStatusChangedEmailBody(OrderStatusChangedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("      ORDER STATUS UPDATE NOTIFICATION\n");
        sb.append("===========================================\n\n");
        sb.append("Order ID: #").append(event.getOrderId()).append("\n");
        sb.append("User ID: ").append(event.getUserId()).append("\n");
        sb.append("Updated At: ").append(event.getUpdatedAt()).append("\n\n");
        
        sb.append("-------------------------------------------\n");
        sb.append("STATUS CHANGE:\n");
        sb.append("-------------------------------------------\n");
        sb.append("Previous Status: ").append(event.getPreviousStatus()).append("\n");
        sb.append("New Status: ").append(event.getNewStatus()).append("\n");
        sb.append("-------------------------------------------\n\n");
        
        sb.append("Thank you for using Marketplace Lite!\n");
        
        return sb.toString();
    }
}
