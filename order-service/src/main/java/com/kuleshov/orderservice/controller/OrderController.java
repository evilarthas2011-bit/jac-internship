package com.kuleshov.orderservice.controller;

import com.kuleshov.orderservice.dto.CreateOrderRequest;
import com.kuleshov.orderservice.dto.OrderResponse;
import com.kuleshov.orderservice.dto.PageResponse;
import com.kuleshov.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal String userEmail
    ) {
        log.info("POST /api/v1/orders for user: {}", userEmail);
        OrderResponse response = orderService.createOrder(request, userEmail);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal String userEmail
    ) {
        return ResponseEntity.ok(orderService.getOrderById(id, userEmail));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal String userEmail,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(userEmail, pageable));
    }
}