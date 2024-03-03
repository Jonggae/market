package com.example.market.order.repository;

import com.example.market.order.entity.Order;
import com.example.market.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    void deleteAllByOrder(Order order);
}
