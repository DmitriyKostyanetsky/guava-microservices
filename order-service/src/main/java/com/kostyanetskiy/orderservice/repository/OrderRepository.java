package com.kostyanetskiy.orderservice.repository;

import com.kostyanetskiy.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCode(String code);
    List<Order> findByUserId(Long userId);
}
