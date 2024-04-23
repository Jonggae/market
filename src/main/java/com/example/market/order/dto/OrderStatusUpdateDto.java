package com.example.market.order.dto;

import com.example.market.order.entity.Order;

public class OrderStatusUpdateDto {
    private Order.OrderStatus status;

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }
}
