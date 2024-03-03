package com.example.market.order.dto;

import com.example.market.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long customerId;
    private List<OrderItemDto> orderItems;
    private LocalDateTime orderDateTime; // 주문 시각
    private Order.OrderStatus status;

}
