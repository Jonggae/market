package com.example.market.order.dto;

import com.example.market.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;
    private Long customerId;
    private LocalDateTime orderDateTime; // 주문 시각
    private List<OrderItemDto> orderItems;
    private Order.OrderStatus status;

    public static OrderDto from(Order order) {
        List<OrderItemDto> orderItemsDto = order.getOrderItems().stream()
                .map(OrderItemDto::from)
                .collect(Collectors.toList());

        return OrderDto.builder()
                .orderId(order.getId())
                .customerId(order.getCustomer().getId())
                .orderItems(orderItemsDto)
                .status(order.getOrderStatus())
                .orderDateTime(order.getOrderDate())
                .build();
    }

}
