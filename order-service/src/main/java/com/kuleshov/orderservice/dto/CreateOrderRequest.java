package com.kuleshov.orderservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateOrderRequest(


        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.01", message = "Total amount must be at least 0.01")
        @Digits(integer = 17, fraction = 2,
                message = "Total amount must have at most 17 integer digits and 2 fraction digits")
        BigDecimal totalAmount
) {
}
