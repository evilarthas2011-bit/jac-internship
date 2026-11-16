package com.kuleshov.orderservice.repository;

import com.kuleshov.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserEmail(String userEmail, Pageable pageable);

    Optional<Order> findByIdAndUserEmail(Long id, String userEmail);
}