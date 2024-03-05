package com.example.market.order.repository;

import com.example.market.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCustomerId(Long customerId);
    List<Order> findAllByCustomerId(Long customerId);

    Optional<Order> findByCustomerIdAndOrderStatus(Long customerId, Order.OrderStatus orderStatus);

    Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId);
}
