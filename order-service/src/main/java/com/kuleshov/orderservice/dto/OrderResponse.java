package com.kuleshov.orderservice.dto;

import com.kuleshov.orderservice.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String userEmail,
        String description,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserEmail(),
                order.getDescription(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
