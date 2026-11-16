package com.kuleshov.orderservice.service;

import com.kuleshov.orderservice.dto.CreateOrderRequest;
import com.kuleshov.orderservice.dto.OrderResponse;
import com.kuleshov.orderservice.dto.PageResponse;
import com.kuleshov.orderservice.entity.Order;
import com.kuleshov.orderservice.enums.OrderStatus;
import com.kuleshov.orderservice.exception.OrderNotFoundException;
import com.kuleshov.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        log.info("Creating order for user: {}", userEmail);

        Order order = Order.builder()
                .userEmail(userEmail)
                .description(request.description())
                .totalAmount(request.totalAmount())
                .status(OrderStatus.CREATED)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order created with id: {}", saved.getId());

        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, String userEmail) {
        Order order = orderRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(String userEmail, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserEmail(userEmail, pageable);
        return PageResponse.from(orders, OrderResponse::from);
    }
}