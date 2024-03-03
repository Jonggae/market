package com.example.market.order.controller;

import com.example.market.order.dto.OrderDto;
import com.example.market.order.dto.OrderItemDto;
import com.example.market.order.entity.Order;
import com.example.market.order.entity.Order.OrderStatus;
import com.example.market.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 내 주문 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrderList(@PathVariable Long customerId) {
        List<OrderDto> orderDto = orderService.getOrderList(customerId);
        return ResponseEntity.ok(orderDto);
    }

    // 주문 항목 추가
    @PostMapping("/{customerId}/items")
    public ResponseEntity<OrderItemDto> addOrderItem(@PathVariable Long customerId,
                                                     @RequestBody OrderItemDto orderItemDto) {
        OrderItemDto newOrderItem = orderService.addOrderItem(customerId, orderItemDto);
        return ResponseEntity.ok(newOrderItem);
    }

    // 주문 확정
    @PostMapping("/{customerId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long customerId){
        Order confirmOrder = orderService.confirmOrder(customerId);
        return ResponseEntity.ok(confirmOrder);
    }

    // 주문 상태 업데이트
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
                                                  @RequestParam OrderStatus newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok().build();
    }

    // 주문 항목 수량 변경
    @PutMapping("/{customerId}/items/{orderItemId}")
    public ResponseEntity<List<OrderDto>> updateOrderItemQuantity(@PathVariable Long customerId, @PathVariable Long orderItemId, @RequestBody OrderItemDto orderItemDto) {
        List<OrderDto> updatedOrders = orderService.updateOrderItemQuantity(customerId, orderItemId, orderItemDto);
        return ResponseEntity.ok(updatedOrders);
    }

    // 주문 항목 삭제
    @DeleteMapping("/items/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        orderService.deleteOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
